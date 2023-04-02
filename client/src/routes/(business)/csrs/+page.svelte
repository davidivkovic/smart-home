<script>
  import CSRDialog from '$lib/components/csr/CSRDialog.svelte'
  import { openDialog } from '$lib/stores/appStore'
  import { CSRFromPEM } from '$lib/crypto/csr'

  // openDialog(CSRDialog, { hasCloseButton: false })

  const testCSR = CSRFromPEM(
`-----BEGIN CERTIFICATE REQUEST-----
MIIB1TCCAT4CAQAwgZQxIjAgBgkqhkiG9w0BCQEME2dvcmFqa29yYUBnbWFpbC5j
b20xCzAJBgNVBAYMAkdCMQ8wDQYDVQQIDAZMb25kb24xDzANBgNVBAcMBkxvbmRv
bjEUMBIGA1UECwwLRW5naW5lZXJpbmcxETAPBgNVBAoMCEZhY2Vib29rMRYwFAYD
VQQDDA1EYXZpZCBJdmtvdmljMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDW
BEeoy92vIC6QAgYjky1DyHQ4ElweWsOb45yBYbJJyeldA4iftyZgVq86y6DbA/1V
tiaZZiQnCVS43St5dvWYf4D8Cytp1Yth/eSBtWpFCkPdJSV++3dAKKQXNfLhkB91
2bLl2Ia3Q1Ntk2IUqXsp3SsHtdKySH8fh2PE+wLtVwIDAQABoAAwDQYJKoZIhvcN
AQELBQADgYEATEMVVI1jGyEGpYsjrWQtWjcFCkPwi6xOj3tuQ7/zApMnN8Eacjcz
2Crm4cNIVLLcv4XBo0WN/uoHEOnEjxdILmBNWPjFqxYc5gbJXAlb0j0wz0fYgf8M
WgrcY17UiYFc8tIJjqlZrAcA7e4CZAHBFCyFB28KyknzZ252P+rslGI=
-----END CERTIFICATE REQUEST-----`
  )

  const csrs = [testCSR]

</script>

<main>
  {#each csrs as csr}
    <p>{csr.commonName}</p>
    <p>{csr.country}</p>
  {/each}
</main>
