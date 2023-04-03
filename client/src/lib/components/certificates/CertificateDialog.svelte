<script>
  import { RDN } from '$lib/crypto/csr'
  import CertificateIcon from '~icons/tabler/certificate'
  import { KeyUsagesExtension, ExtendedKeyUsageExtension } from '@peculiar/x509'
  import dayjs from 'dayjs'
  import { openDialog } from '$lib/stores/appStore'
  import { checkValidity } from '$lib/api/certificates'
  import { user, isAdmin } from '$lib/stores/userStore'

  /**
   * @type {{
   *  alias: string,
   *  chain: import('@peculiar/x509').X509Certificate[],
   *  chainValidity: { 'serialNumber': boolean },
   *  checkedAt: Date,
   *  PEMChain: string
   * }}
   */
  export let certificate

  const certificates = certificate.chain.reverse()
  const checkedAt = dayjs(certificate.checkedAt).format('HH:mm')

  let selectedCertificateIndex = certificates.length - 1
  let selectedFieldIndex = -1
  let indented = false

  $: validity = certificate.chainValidity
  $: selectedCertificate = certificates[selectedCertificateIndex]
  $: subjectFields = Object.values(RDN)
    .map((key) => ({
      name: key,
      value: selectedCertificate.subjectName.getField(key)[0]
    }))
    .filter((field) => field.value)
    .map((field) => `${field.name}=${field.value}`)
    .join(', ')

  $: issuerFields = Object.values(RDN)
    .map((key) => ({
      name: key,
      value: selectedCertificate.issuerName.getField(key)[0]
    }))
    .filter((field) => field.value)
    .map((field) => `${field.name}=${field.value}`)
    .join(', ')

  $: certificateFields = [
    {
      name: 'Serial Number',
      value: selectedCertificate.serialNumber
    },
    {
      name: 'Issuer',
      value: issuerFields
    },
    {
      name: 'Subject',
      value: subjectFields
    },
    {
      name: 'Valid From',
      value: selectedCertificate.notBefore.toLocaleString()
    },
    {
      name: 'Valid Until',
      value: selectedCertificate.notAfter.toLocaleString()
    },
    {
      name: 'Key Usage',
      value: selectedCertificate.extensions
        .map((e) => (e instanceof KeyUsagesExtension ? e.toTextObject()[''] : null))
        .filter((e) => e)
    },
    {
      name: 'Extended Key Usage',
      value: selectedCertificate.extensions
        .map((e) => (e instanceof ExtendedKeyUsageExtension ? e.toTextObject()[''] : null))
        .filter((e) => e)
    },
    {
      name: 'CRL Dist. Point',
      value: (() => {
        var crlDistributionExt = selectedCertificate.extensions.find(
          (ext) => ext.type === '2.5.29.31'
        )
        return crlDistributionExt ? ab2str(crlDistributionExt.value.slice(10)) : null
      })()
    },
    {
      name: 'Signature Algorithm',
      value: selectedCertificate.signatureAlgorithm.name
    },
    {
      name: 'Hash Algorithm',
      value: selectedCertificate.signatureAlgorithm.hash.name
    }
  ].filter((field) => field.value && field.value.length > 0)

  const selectCertificate = (index) => {
    selectedCertificateIndex = index
  }

  const openRevokeDialog = async () => {
    const RevokeDialog = await import('./RevokeCertificateDialog.svelte').then(($) => $.default)
    openDialog(RevokeDialog, { certificate: selectedCertificate }, async () => {
      certificate.chainValidity = await checkValidity(certificate.PEMChain)
    })
  }

  const ab2str = (buf) => {
    return String.fromCharCode.apply(null, new Uint8Array(buf))
  }

  const createDownload = (content, name, fileType) => { 
    const file = new Blob([content], { type: 'text/plain' })
    const href = URL.createObjectURL(file)
    const fileName = `${name}.${fileType}`

    return { href, fileName }
  }

  let adminDownload

  $: certificateIsMine = certificate.chain.at(-1).subjectName.getField('0.9.2342.19200300.100.1.1')[0] === $user.id
  
  $: if ($isAdmin || !certificateIsMine) {
    adminDownload = createDownload(
      certificate.PEMChain, 
      certificate.chain.at(-1).subjectName.getField(RDN.commonName),
      'crt'
    )
  }

  const openDownloadDialog = async () => {
    const DownloadDialog = await import('./DownloadCertificateDialog.svelte').then(($) => $.default)
    openDialog(DownloadDialog, { createDownload, chain: certificate.chain, PEMChain: certificate.PEMChain })
  }
  

  // const download = () => {
  //   if ($isAdmin) {

  //   }
  // }
</script>

<div class="w-[500px] px-1 py-1">
  <h3 class="text-xl">Certification Chain</h3>
  <p class="text-sm text-neutral-600">
    A valid certificate should lead to a trusted root through its chain
  </p>

  <div class="mt-5 border border-neutral-300">
    {#each certificates as certificate, index}
      <div class="relative transition {selectedCertificateIndex === index && 'bg-neutral-100'}">
        <button
          on:click={() => selectCertificate(index)}
          style="padding-left: {(indented ? index * 32 : 0) + 16}px;"
          class="flex w-full bg-transparent py-3 text-left font-normal"
        >
          <CertificateIcon class="mt-px h-5 w-5" />
          <div class="ml-2.5">
            <h3 class="text-sm font-medium {selectedCertificateIndex === index && ''}">
              {certificate.subjectName.getField(RDN.commonName)}
            </h3>
            <p class="text-[13px] text-neutral-600">
              Valid from
              {dayjs(certificate.notBefore).format('MMM D, YYYY')}
              to
              {dayjs(certificate.notAfter).format('MMM D, YYYY')}
            </p>
            <small class="text-neutral-500">
              Issued by {certificates[Math.max(0, index - 1)].subjectName.getField(RDN.commonName)}
            </small>
          </div>
          <div class="ml-auto -mr-1">
            <div class="flex items-center justify-end">
              <p class="mr-2 text-sm font-medium">
                {validity[certificate.serialNumber] ? 'Valid' : 'Not Valid'}
              </p>
              <div
                class="h-2 w-2 rounded-full {validity[certificate.serialNumber]
                  ? 'bg-green-500'
                  : 'bg-red-500'}"
              />
            </div>
            <small class="text-right">Checked at {checkedAt}</small>
          </div>
        </button>
      </div>
    {/each}
  </div>
  <!-- <div class="flex mt-2 justify-end mb-1.5">
    <label for="indented-chain-cb" class="text-[13px] select-none cursor-pointer">Hierarchy</label>
    <input
      id="indented-chain-cb"
      type="checkbox"
      bind:checked={indented}
      class="ml-2 cursor-pointer"
    />
  </div> -->
  <div class=" mt-4 gap-x-3">
    <div class="flex-1 border border-neutral-300">
      <h4 class="border-b border-neutral-300 bg-neutral-50 px-3 py-2 text-sm font-medium">
        Certificate Data
      </h4>
      <table class="">
        <tbody>
          {#each certificateFields as field, index}
            <!-- <div class="flex"> -->
            <tr
              on:click={() => (selectedFieldIndex = index)}
              class="cursor-pointer border-b border-neutral-200 transition {selectedFieldIndex ===
                index && 'bg-neutral-100'}"
            >
              <td class="py-1.5 pl-3">
                <p class="w-max text-[13px] font-medium text-black">{field.name}:</p>
              </td>
              <td class="py-1.5 pr-3">
                {#if typeof field.value === 'string' && field.value.startsWith('http')}
                  <a
                    href={field.value}
                    class="ml-1.5 text-[13px] text-[#0000EE] visited:font-medium visited:text-[#551A8B] hover:underline"
                  >
                    {field.value}
                  </a>
                {:else}
                  <p class="ml-1.5 text-[13px]">{field.value}</p>
                {/if}
              </td>
            </tr>
            <!-- </div> -->
          {/each}
        </tbody>
      </table>
    </div>
  </div>
  {#if selectedCertificateIndex === certificates.length - 1}
  <div class="mt-5 flex gap-x-2">
    {#if $isAdmin || !certificateIsMine}
      <a 
        href={adminDownload.href} 
        download={adminDownload.fileName} 
        class="px-6 py-3 text-center font-medium transition duration-300 focus-visible:outline-none border border-black bg-white text-[15px] text-black hover:bg-neutral-100 disabled:bg-neutral-200 disabled:text-neutral-500 flex-1 !text-sm"
      >
        Download Certificate
      </a>
    {:else}
      <button class="secondary flex-1 !text-sm" on:click={openDownloadDialog}>Download Certificate</button>
    {/if}
    {#if $isAdmin && validity[selectedCertificate.serialNumber]}
      <button class="primary flex-1 !text-sm" on:click={openRevokeDialog}>
        Revoke Certificate
      </button>
    {/if}
  </div>
  {/if}
</div>
