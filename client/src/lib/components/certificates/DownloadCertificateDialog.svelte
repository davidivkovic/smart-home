<script>
  import { RDN } from '$lib/crypto/csr'
  import { PemConverter } from '@peculiar/x509'

  /**
   * @type {import('@peculiar/x509').X509Certificate[]}
   */
  export let chain
  export let PEMChain
  export let createDownload
  export let close

  let fileInput
  let fileName = ''
  let certificateValid = false
  let download = { href: '', fileName: '' }

  const previewFile = () => {
    const reader = new FileReader()
    const file = fileInput.files[0]
    
    reader.onloadend = function() {
      const key = PemConverter.decodeFirst(reader.result)
      crypto.subtle.importKey('pkcs8', key, { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' }, true, ['sign'])
        .then(async (privateKey) => {
          // console.log('valid')
          // console.log(privateKey)
          const publicKey = await crypto.subtle.exportKey("spki", await chain.at(-1).publicKey.export())
          const privateKeyData = await crypto.subtle.exportKey("pkcs8", privateKey)

          const certificatePublicKeyHash = await crypto.subtle.digest("SHA-256", publicKey)
          const privateKeyDataHash = await crypto.subtle.digest("SHA-256", privateKeyData)

          const match = window.btoa(certificatePublicKeyHash) === window.btoa(privateKeyDataHash)
          // console.log(match)

          if (match) {
            certificateValid = true
            download = createDownload(PEMChain + reader.result, chain.at(-1).subjectName.getField(RDN.commonName), 'crt')
          }
          else {
            certificateValid = false
            download = { href: '', fileName: '' }
          }
        })
        .catch((e) => {
          certificateValid = false
          download = { href: '', fileName: '' }
          // console.log(e)
          // console.log('invalid')
        })
      
    }

    if (file && (file.name.endsWith('.key') || file.name.endsWith('.pem'))) {
      reader.readAsText(file)
      fileName = file.name
    } 
    else {
      fileName = ''
    }
  }
</script>

<div class="w-[400px]">
  <h4 class="font-medium text-lg">Download certificate</h4>
  <p class="text-[13px] text-neutral-500 leading-[18px]">
    In order to download a certificate that can be used for authentication
    you will need to upload the corresponding private key that you downloaded
    from the signing request. Please upload the private key below.
  </p>
  <div class="flex items-center justify-center w-full mt-5 mb-5">
    <label for="dropzone-file" class="flex flex-col items-center justify-center w-full h-36 border-2 border-neutral-300 border-dashed cursor-pointer bg-neutral-50 hover:bg-neutral-100">
        <div class="flex flex-col items-center justify-center pt-5 pb-6">
            {#if !fileName}
              <svg aria-hidden="true" class="w-8 h-8 mb-3 text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path></svg>
              <p class="mb-2 text-sm text-neutral-500"><span class="font-semibold">Click to upload</span> or drag and drop</p>
              <p class="text-xs text-neutral-500">.KEY or .PEM (Max. size 1MB)</p>
            {:else}
              <svg aria-hidden="true" class="w-8 h-8 mb-3 text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
              <p class="mb-2 text-sm text-neutral-500"><span class="font-semibold">{fileName}</span></p>
            {/if}
        </div>
        <input bind:this={fileInput} id="dropzone-file" type="file" class="hidden" on:change={previewFile} accept=".key, .pem" />
    </label>
  </div> 
  <a 
    href={download.href}
    download={download.fileName}
    class="flex hover:bg-neutral-900 bg-black justify-center py-3.5 text-white text-sm font-medium w-full cursor-pointer {!certificateValid && 'bg-zinc-200 text-zinc-500 pointer-events-none'}"
  >
    Download Certificate (.crt)
  </a>
</div>