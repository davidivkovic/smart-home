<script>
  import { certFromPEM, chainFromPEM } from '$lib/crypto/cert'
  import { RDN } from '$lib/crypto/csr'
  import { approveCSR } from '$lib/api/csrs'
  import { openDialog } from '$lib/stores/appStore'
  import CertificateDialog from '../certificates/CertificateDialog.svelte'

  export let csr
  export let caCertificatesPEM
  export let close

  const keyUsages = [
    {
      name: 'Digital Signature',
      value: 0,
      checked: false
    },
    {
      name: 'Non Repudiation',
      value: 1,
      checked: false
    },
    {
      name: 'Key Encipherment',
      value: 2,
      checked: false
    },
    {
      name: 'Data Encipherment',
      value: 3,
      checked: false
    },
    {
      name: 'Key Agreement',
      value: 4,
      checked: false
    },
    {
      name: 'Key Cert Sign',
      value: 5,
      checked: false
    },
    {
      name: 'CRL Sign',
      value: 6,
      checked: false
    },
    {
      name: 'Encipher Only',
      value: 7,
      checked: false
    },
    {
      name: 'Decipher Only',
      value: 8,
      checked: false
    }
  ]

  const extendedKeyUsages = [
    {
      name: 'Server Authentication',
      value: 1,
      checked: false
    },
    {
      name: 'Client Authentication',
      value: 2,
      checked: false
    },
    {
      name: 'Code Signing',
      value: 3,
      checked: false
    },
    {
      name: 'Email Protection',
      value: 4,
      checked: false
    },
    {
      name: 'Time Stamping',
      value: 8,
      checked: false
    },
    {
      name: 'OCSP Signing',
      value: 9,
      checked: false
    }
  ]

  function toggleKeyUsage(keyUsage) {
    keyUsage.checked = !keyUsage.checked
  }

  function uncheckAllKeyUsages() {
    keyUsages.forEach((keyUsage) => (keyUsage.checked = false))
    extendedKeyUsages.forEach((keyUsage) => (keyUsage.checked = false))
  }

  function setServerTemplate() {
    uncheckAllKeyUsages()
    keyUsages[0].checked = true
    keyUsages[2].checked = true
    extendedKeyUsages[0].checked = true
  }

  function setClientTemplate() {
    uncheckAllKeyUsages()
    keyUsages[0].checked = true
    keyUsages[2].checked = true
    extendedKeyUsages[1].checked = true
  }

  function setCodeSigningTemplate() {
    uncheckAllKeyUsages()
    keyUsages[0].checked = true
    extendedKeyUsages[2].checked = true
  }

  $: hasAnyUsage =
    keyUsages.some((keyUsage) => keyUsage.checked) ||
    extendedKeyUsages.some((keyUsage) => keyUsage.checked)

  /**
   * @type {{
   *  alias: string,
   *  data: import('@peculiar/x509').X509Certificate
   * }[]}
   */
  const caCertificates = Object.entries(caCertificatesPEM).map(([alias, pem]) => ({
    alias,
    data: certFromPEM(pem)
  }))

  let signingData = {
    issuerAlias: caCertificates[0].alias,
    subjectAlias: '',
    duration: '8640'
  }

  async function submit() {
    try {
      const pemChain = await approveCSR(csr.id, {
        issuerAlias: signingData.issuerAlias,
        subjectAlias: signingData.subjectAlias,
        hoursValid: Number(signingData.duration),
        keyUsage: keyUsages
          .filter((keyUsage) => keyUsage.checked)
          .map((keyUsage) => keyUsage.value),
        extendedKeyUsage: extendedKeyUsages
          .filter((keyUsage) => keyUsage.checked)
          .map((keyUsage) => keyUsage.value)
      })
      // const CertificateDialog = await import('../certificates/CertificateDialog.svelte').then(
      //   ($) => $.default
      // )
      close('signed')
      // openDialog(CertificateDialog, {
      //   certificate: {
      //     alias: signingData.subjectAlias,
      //     chain: chainFromPEM(pemChain)
      //   }
      // })
    } catch (e) {
      console.error(e)
    }
  }
</script>

<form on:submit|preventDefault={submit} class="max-w-sm">
  <h3 class="mb-4">Sign Certificate Request</h3>
  <div class="mb-2 flex gap-x-2">
    <div>
      <label for="issuer-alias">Issuing Certificate</label>
      <select id="issuer-alias" bind:value={signingData.issuerAlias}>
        {#each caCertificates as caCertificate}
          <option value={caCertificate.alias}>
            {caCertificate.data.subjectName.getField(RDN.commonName)}
          </option>
        {/each}
      </select>
    </div>
    <div>
      <label for="cert-duration">Expires in</label>
      <select id="cert-duration" class="cursor-pointer" bind:value={signingData.duration}>
        <option value="720">1 month</option>
        <option value="2160">3 months</option>
        <option value="4320">6 months</option>
        <option value="8640">1 year</option>
        <option value="17280">2 years</option>
        <option value="25920">3 years</option>
      </select>
    </div>
  </div>
  <label for="subject-alias">Subject Certificate Alias</label>
  <input required type="text" id="subject-alias" bind:value={signingData.subjectAlias} placeholder="example.com, John Doe..." />
  <small class="mt-0.5 text-neutral-500">Can only contain letters, numbers and symbols "." and "-"</small>
  <h4 class="mt-3 mb-0.5 text-sm font-medium">Templates</h4>
  <div class="flex gap-x-2">
    <button
      type="button"
      class="secondary h-9 !border-neutral-300 !bg-neutral-100 !py-1 !text-[13px]"
      on:click={setServerTemplate}>Server</button
    >
    <button
      type="button"
      class="secondary h-9 !border-neutral-300 !bg-neutral-100 !py-1 !text-[13px]"
      on:click={setClientTemplate}>Client</button
    >
    <button
      type="button"
      class="secondary h-9 !border-neutral-300 !bg-neutral-100 !py-1 !text-[13px]"
      on:click={setCodeSigningTemplate}>Code Signing</button
    >
  </div>
  <div class="mt-4 flex gap-x-10">
    <div>
      <h6 class="mb-3 text-sm font-medium">Key Usages</h6>
      {#each keyUsages as keyUsage}
        <div class="mb-2 flex">
          <input
            type="checkbox"
            id="key-usage-{keyUsage.value}"
            bind:checked={keyUsage.checked}
            class="mr-1.5 cursor-pointer"
          />
          <label for="key-usage-{keyUsage.value}" class="cursor-pointer select-none text-[13px]">
            {keyUsage.name}
          </label>
        </div>
      {/each}
    </div>
    <div>
      <h6 class="mb-3 text-sm font-medium">Extended Key Usages</h6>
      {#each extendedKeyUsages as keyUsage}
        <div class="mb-2 flex">
          <input
            type="checkbox"
            id="xt-key-usage-{keyUsage.value}"
            bind:checked={keyUsage.checked}
            class="mr-1.5 cursor-pointer"
          />
          <label for="xt-key-usage-{keyUsage.value}" class="cursor-pointer select-none text-[13px]">
            {keyUsage.name}
          </label>
        </div>
      {/each}
    </div>
  </div>
  <div class="mt-4 flex gap-x-2">
    <button 
      type="submit" 
      class="primary flex-1 !text-sm" 
      disabled={!hasAnyUsage}
    >
      Sign Certificate
    </button>
    <button type="button" class="secondary px-10 !text-sm" on:click={close}>Cancel</button>
  </div>
</form>
