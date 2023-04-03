<script>
  import 'flag-icons/css/flag-icons.min.css'
  import { getNames, registerLocale } from 'i18n-iso-countries'
  import en from 'i18n-iso-countries/langs/en.json'
  import CheckIcon from '~icons/tabler/circle-check-filled'
  import DiscountCheckIcon from '~icons/tabler/discount-check'
  import { RDN, createCSR } from '$lib/crypto/csr'
  import { submitCSR } from '$lib/api/csrs'
  import { openDialog } from '$lib/stores/appStore'
  import CSRResultDialog from './CSRResultDialog.svelte'
  import CSRDetailsDialog from './CSRDetailsDialog.svelte'
  export let close

  registerLocale(en)

  let fields = [
    {
      name: 'Name',
      rdnName: 'Common Name',
      rdn: RDN.commonName,
      description:
        'Use the fully qualified domain name (FQDN) of your server (with or without the WWW).',
      type: 'text',
      maxlength: 64,
      value: ''
    },
    {
      name: 'Company',
      rdnName: 'Organization Name',
      rdn: RDN.organization,
      description:
        'Legal name of your company/organization (i.e. Acme Security, LLC). For personal certificates, just use your name.',
      type: 'text',
      maxlength: 64,
      value: ''
    },
    {
      name: 'Division',
      rdnName: 'Organization Unit',
      rdn: RDN.organizationalUnit,
      description: 'Division of your organization handling the certificate (i.e. IT department).',
      type: 'text',
      maxlength: 64,
      value: ''
    },
    {
      name: 'City',
      rdnName: 'Locality',
      rdn: RDN.locality,
      description: 'The city that you are located in (i.e. Tampa).',
      type: 'text',
      maxlength: 128,
      value: ''
    },
    {
      name: 'State',
      rdnName: 'State or Province',
      rdn: RDN.state,
      description: 'The state or province in which you are located in (i.e. Florida).',
      type: 'text',
      maxlength: 128,
      value: ''
    },
    {
      name: 'Country',
      rdnName: 'Country Code',
      rdn: RDN.country,
      description: 'Choose your country (i.e. United States).',
      type: 'select',
      values: Object.entries(getNames('en', { select: 'official' })).sort((a, b) =>
        a[1].localeCompare(b[1])
      ),
      value: ''
    },
    {
      name: 'Email',
      rdnName: 'Email Address',
      rdn: RDN.email,
      description: 'Please enter your e-mail address.',
      type: 'email',
      value: ''
    }
  ]

  const onSubmit = async (e) => {
    const rdnValues = fields.reduce(
      (previous, field) => ({ [field.rdn]: field.value, ...previous }),
      {}
    )
    const csr = await createCSR(rdnValues)
    try {
      const csrResult = await submitCSR(csr.PEM)
      close()
      openDialog(
        CSRResultDialog, 
        { csr: csrResult, privateKey: csr.privateKey, hasCloseButton: false }, 
        () => {}
      )
    }
    catch (e) {
      console.error(e)
    }
  }
</script>

<main class="mx-auto pl-3 pr-6 pt-1.5 pb-3">
  <div class="flex">
    <!-- <div class="basis-6"></div> -->
    <div class="ml-2 w-[440px]">
      <h3>Submit a certificate signing request</h3>
      <p class="text-[13px] leading-[18px] text-neutral-500">
        A certificate signing request (CSR) and a private key will be generated on your behalf. The
        signing request is sent to us for processing, while the private key is kept to yourself.
      </p>
    </div>
  </div>
  <form on:submit|preventDefault={onSubmit} class="mt-7 space-y-4">
    {#each fields as field}
      <div class="flex">
        <div class="mr-3 basis-36 text-right">
          <label for={field.name} class="text-sm font-medium">{field.name}:</label>
          <small class="-mt-0.5">{field.rdn} - {field.rdnName}</small>
        </div>
        <div>
          <div class="relative flex w-[340px] items-center">
            {#if field.type === 'text'}
              <input
                required
                type="text"
                id={field.name}
                bind:value={field.value}
                maxlength={field.maxlength}
                class="peer h-10 max-w-[300px] text-sm"
              />
            {:else if field.type === 'email'}
              <input
                required
                type="email"
                id={field.name}
                bind:value={field.value}
                maxlength={field.maxlength}
                class="peer h-10 max-w-[300px] text-sm"
              />
            {:else}
              <select
                required
                bind:value={field.value}
                class="peer h-10 max-w-[300px] cursor-pointer truncate pr-16 text-sm"
              >
                {#each field.values as value}
                  <option value={value[0]}>{value[1]}</option>
                {/each}
              </select>
              <div class="pointer-events-none absolute right-[72px]">
                <div class="fi fi-{field.value.toLowerCase()}" />
              </div>
            {/if}
            <CheckIcon
              class="ml-3 hidden h-4 w-4 text-[#40c82b] peer-valid:block peer-valid:peer-focus-within:hidden"
            />
          </div>
          <p class="w-[310px] pt-0.5 text-xs text-neutral-500">{field.description}</p>
        </div>
      </div>
    {/each}
    <div class="flex">
      <div class="basis-36" />
      <button
        type="submit"
        class="secondary -ml-0.5 mt-4 flex w-[300px] items-center justify-center gap-x-2 !py-2.5 !text-sm"
      >
        <span class="ml-2">Submit Request</span>
        <DiscountCheckIcon class="h-5 w-5" />
      </button>
    </div>
  </form>
</main>
