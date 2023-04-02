<script>
  import { onMount } from 'svelte'

  import { getCsrs } from '$lib/api/csrs'

  import { openDialog } from '$lib/stores/appStore'
  import CsrDialog from '$lib/components/csr/CSRDialog.svelte'
  import CsrResultDialog from '$lib/components/csr/CSRResultDialog.svelte'
  import CsrPreview from '$lib/components/csr/CSRPreview.svelte'

  // openDialog(CsrDialog, { hasCloseButton: false })
  // openDialog(CsrResultDialog, { hasCloseButton: false })

  //   const testCSR = CSRFromPEM(
  // `-----BEGIN CERTIFICATE REQUEST-----
  // MIIB1TCCAT4CAQAwgZQxIjAgBgkqhkiG9w0BCQEME2dvcmFqa29yYUBnbWFpbC5j
  // b20xCzAJBgNVBAYMAkdCMQ8wDQYDVQQIDAZMb25kb24xDzANBgNVBAcMBkxvbmRv
  // bjEUMBIGA1UECwwLRW5naW5lZXJpbmcxETAPBgNVBAoMCEZhY2Vib29rMRYwFAYD
  // VQQDDA1EYXZpZCBJdmtvdmljMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDW
  // BEeoy92vIC6QAgYjky1DyHQ4ElweWsOb45yBYbJJyeldA4iftyZgVq86y6DbA/1V
  // tiaZZiQnCVS43St5dvWYf4D8Cytp1Yth/eSBtWpFCkPdJSV++3dAKKQXNfLhkB91
  // 2bLl2Ia3Q1Ntk2IUqXsp3SsHtdKySH8fh2PE+wLtVwIDAQABoAAwDQYJKoZIhvcN
  // AQELBQADgYEATEMVVI1jGyEGpYsjrWQtWjcFCkPwi6xOj3tuQ7/zApMnN8Eacjcz
  // 2Crm4cNIVLLcv4XBo0WN/uoHEOnEjxdILmBNWPjFqxYc5gbJXAlb0j0wz0fYgf8M
  // WgrcY17UiYFc8tIJjqlZrAcA7e4CZAHBFCyFB28KyknzZ252P+rslGI=
  // -----END CERTIFICATE REQUEST-----`
  //   )

  let currentPage = 1
  let end = false

  let csrs = []

  onMount(async () => {
    document.addEventListener('scroll', onScroll)
    csrs = await getCsrs(1)
  })

  const onScroll = async (event) => {
    const { scrollTop, clientHeight, scrollHeight } = event.target.scrollingElement
    if (scrollTop + clientHeight >= scrollHeight) {
      currentPage += 1
      const newData = await getCsrs(currentPage)
      if (newData.length === 0) {
        document.removeEventListener('scroll', onScroll)
        end = true
      } else {
        csrs = [...csrs, newData]
      }
    }
  }
</script>

<h1>Certificate signing requests</h1>
<div class="mt-6 flex flex-col gap-5">
  {#each csrs as csr}
    <CsrPreview {csr} />
  {/each}
</div>
