<script>
  import { goto } from '$app/navigation'
  import { page } from '$app/stores'
  import { certFromPEM, chainFromPEM } from '$lib/crypto/cert'
  import { openDialog } from '$lib/stores/appStore'
  import CertificateDialog from '$lib/components/certificates/CertificateDialog.svelte'
  import { checkValidity, getByAlias } from '$lib/api/certificates'
  import { onMount } from 'svelte'
  import { RDN } from '$lib/crypto/csr'
  import dayjs from 'dayjs'

  export let data

  /**
   * @type {{
   *  alias: string,
   *  data: import('@peculiar/x509').X509Certificate
   * }[]}
   */
  let certificates = []

  $: {
    if (Object.entries(data.certificates).length < 4) end = true
    certificates = [...certificates, ...Object
      .entries(data.certificates)
      .map(([alias, pem]) => ({ alias, data: certFromPEM(pem)}))
    ]
  }

  const cache = new Map()

  async function preloadCertificate(alias) {
    if (cache.has(alias)) return
    const PEMChain = await getByAlias(alias)
    const chainValidity = await checkValidity(PEMChain)
    const chain = chainFromPEM(PEMChain)
    cache.set(alias, { chain, PEMChain, chainValidity, checkedAt: new Date() })
  }

  async function openCertificate(alias) {
    if (!cache.has(alias)) await preloadCertificate(alias)
    const { chain, chainValidity, checkedAt, PEMChain } = cache.get(alias)
    openDialog(
      CertificateDialog,
      { certificate: { alias, chain, PEMChain, chainValidity, checkedAt }},
      () => invalidateCertificate(alias)
    )
  }

  function invalidateCertificate(alias) {
    cache.clear()
    // cache.delete(alias)
  }

  onMount(() => {
    cache.clear()
  })

  let end = false
  
  $: currentPage = Number($page.url.searchParams.get('page') ?? 1)

  const nextPage = async (event) => {
    if (!end) goto(`/certificates?page=${currentPage + 1}`, { noScroll: true })
  }
</script>

<main>
  <div class="mb-6">
    <h1 class="text-xl">Certificates</h1>
    <p class="text-sm">Click on a certificate below to see details or download it</p>
  </div>
  {#each certificates as certificate}
    <button 
      on:mouseenter={() => preloadCertificate(certificate.alias)} 
      on:click={() => openCertificate(certificate.alias)} 
      class="block w-full p-5 border border-neutral-300 bg-white transition duration-300 hover:border-black mb-4 font-normal text-left"
    >
    <div class="">
      <p class="font-semibold text-black">
        {certificate.data.subjectName.getField(RDN.commonName)}
      </p>
      <p class="text-sm text-neutral-600">
        Valid from
        {dayjs(certificate.data.notBefore).format('MMM D, YYYY')}
        to
        {dayjs(certificate.data.notAfter).format('MMM D, YYYY')}
      </p>
      <!-- <small class="text-neutral-500">
        Issued by {certificates[Math.max(0, index - 1)].subjectName.getField(RDN.commonName)}
      </small> -->
    </div>
    </button>
  {/each}
  <div class="mt-2 text-sm font-normal text-neutral-600">
    {#if end}
      <p class="py-3 text-center">No more certificates to show</p>
    {:else}
      <button on:click={nextPage} class="mx-auto flex bg-transparent text-sm font-normal">
        Load More
      </button>
    {/if}
  </div>
</main>