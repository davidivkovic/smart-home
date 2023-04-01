<script>
  import { RDN, createCSR } from '$lib/crypto/csr'
  import { getNames, registerLocale } from 'i18n-iso-countries'
  import en from 'i18n-iso-countries/langs/en.json'
  import 'flag-icons/css/flag-icons.min.css'
  import CheckIcon from '~icons/tabler/circle-check-filled'

  registerLocale(en)

  let fields = [
    {
      name: 'Name',
      rdnName: 'Common Name',
      rdn: RDN.commonName,
      description:
        'Use the fully qualified domain name (FQDN) of your server (with or without the WWW).',
      type: 'text',
      value: ''
    },
    {
      name: 'Company',
      rdnName: 'Organization Name',
      rdn: RDN.organization,
      description:
        'Legal name of your company/organization (i.e. Acme Security, LLC). For personal certificates, just use your name.',
      type: 'text',
      value: ''
    },
    {
      name: 'Division',
      rdnName: 'Organization Unit',
      rdn: RDN.organizationalUnit,
      description: 'Division of your organization handling the certificate (i.e. IT department).',
      type: 'text',
      value: ''
    },
    {
      name: 'City',
      rdnName: 'Locality',
      rdn: RDN.locality,
      description: 'The city that you are located in (i.e. Tampa).',
      type: 'text',
      value: ''
    },
    {
      name: 'State',
      rdnName: 'State or Province',
      rdn: RDN.state,
      description: 'The state or province in which you are located in (i.e. Florida).',
      type: 'text',
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
</script>

<main class="bg-white p-10">
  <form class="space-y-4">
    {#each fields as field}
      <div class="flex">
        <div class="mr-3 basis-36 text-right">
          <label for={field.name} class="text-sm font-medium">{field.name}:</label>
          <small class="-mt-0.5">{field.rdn} - {field.rdnName}</small>
        </div>
        <div class="basis-[350px]">
          {#if field.type === 'text'}
            <input
              required
              type="text"
              id={field.name}
              bind:value={field.value}
              class="h-10 max-w-xs bg-white text-sm peer"
            />
          {:else if field.type === 'email'}
            <input
              required
              type="email"
              id={field.name}
              bind:value={field.value}
              class="h-10 max-w-xs bg-white text-sm peer"
            />
          {:else}
            <div class="relative">
              <select required bind:value={field.value} class="h-10 max-w-xs bg-white text-sm peer">
                {#each field.values as value}
                  <option value={value[0]}>{value[1]}</option>
                {/each}
              </select>
              <div class="fi absolute top-px right-16 fi-{field.value.toLowerCase()}" />
            </div>
          {/if}
          <p class="pt-0.5 text-xs text-neutral-500">{field.description}</p>
        </div>
        {#if field.value}
          <CheckIcon class="text-[#76D516] -ml-4 mt-3 h-4 w-4"/>
        {/if}
      </div>
    {/each}
    <div class="mt-4 max-w-xs">
      <button type="submit" class="bg-black px-10 text-sm text-white">Submit Request</button>
    </div>
  </form>
</main>
