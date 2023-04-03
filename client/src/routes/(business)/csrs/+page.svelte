<script>
  import { onMount } from 'svelte'

  import { openDialog } from '$lib/stores/appStore'
  import CsrResultDialog from '$lib/components/csr/CSRResultDialog.svelte'
  import CsrPreview from '$lib/components/csr/CSRPreview.svelte'
  import { goto } from '$app/navigation'
  import { page } from '$app/stores'

  export let data

  // openDialog(CsrDialog, { hasCloseButton: false })
  // openDialog(CsrResultDialog, { hasCloseButton: false })

  let currentPage = 1
  let end = false
  let loading = false
  let csrs = []

  // onMount(async () => {
  //   // document.addEventListener('scroll', onScroll)
  //   // csrs = await getCsrs(1)
  // })

  // const nextPage = async () => {
  //   if (loading) return
  //   loading = true
  //   currentPage += 1
  //   const newData = await getCsrs(currentPage)
  //   if (newData.length === 0) {
  //     document.removeEventListener('scroll', onScroll)
  //     end = true
  //   } else {
  //     csrs = [...csrs, ...newData]
  //   }
  //   loading = false
  // }

  $: currentPage = Number($page.url.searchParams.get('page') ?? 1)
  $: {
    if (data.csrs.length < 4) end = true
    csrs = [...csrs, ...data.csrs]
  }

  const onScroll = async (event) => {
    // const { scrollTop, clientHeight, scrollHeight } = event.target.scrollingElement
    // if (scrollTop + clientHeight >= scrollHeight) {
    //   await nextPage()
    // }
    if (!end) goto(`/csrs?page=${currentPage + 1}`, { noScroll: true })
  }

  const preloadCsrDialog = async () => {
    await import('$lib/components/csr/CSRDialog.svelte')
  }

  const openCsrDialog = async (csr) => {
    const CsrDialog = await import('$lib/components/csr/CSRDialog.svelte').then(($) => $.default)
    openDialog(CsrDialog)
  }
</script>

<div class="flex">
  <div>
    <h1 class="text-xl">Certificate Signing Requests</h1>
    <p class="text-sm">Click on a CSR below to see details</p>
  </div>
  <button
    on:mouseenter={preloadCsrDialog}
    on:click={openCsrDialog}
    class="secondary ml-auto !text-sm h-11 py-1 px-10 !border-neutral-300 mt-1"
  >
    Create CSR
  </button>
</div>
<div class="mt-6 flex flex-col gap-4">
  {#each csrs as csr}
    <CsrPreview {csr} />
  {/each}
</div>
<div class="mt-2 text-sm font-normal text-neutral-600">
  {#if end}
    <p class="py-3 text-center">No more CSRs to show</p>
  {:else}
    <button on:click={onScroll} class="mx-auto flex bg-transparent text-sm font-normal">
      Load More
    </button>
  {/if}
</div>
