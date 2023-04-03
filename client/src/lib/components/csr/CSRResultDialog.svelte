<script>
  import AlertTriangleIcon from '~icons/tabler/alert-triangle'
  import DownloadIcon from '~icons/tabler/download'
  import { openDialog } from '$lib/stores/appStore'
  import CSRDetailsDialog from './CSRDetailsDialog.svelte'
  export let close
  export let csr
  export let privateKey

  let keyDownloaded = false

  const createDownload = (content, name, fileType) => { 
    const file = new Blob([content], { type: 'text/plain' })
    const href = URL.createObjectURL(file)
    const fileName = `${name}.${fileType}`

    return { href, fileName }
  }

  const privateKeyDownload = createDownload(privateKey, csr.commonName, 'key')
  const csrDownload = createDownload(csr.pemCSR, csr.commonName, 'csr')

  const downloadKey = () => {
    keyDownloaded = true
  }

  const showDetails = () => {
    close()
    openDialog(CSRDetailsDialog, { csr }, close)
  }
</script>

<main class="max-w-[516px] pt-1">
  <div class="border border-[#825c0f]/30 bg-amber-50 p-5 pt-4 text-[#825c0f]">
    <div class="flex items-center gap-x-2">
      <AlertTriangleIcon />
      <h3 class="">Important</h3>
    </div>
    <p class="mt-1.5 text-[13px] leading-[18px] text-[#825c0f]">
      It's impossible to install an SSL certificate if the corresponding private key is lost. This
      application does not store private keys. It's your responsibility to back them up. Protect
      your private key file and do not send it via email or otherwise share it insecurely.
    </p>
  </div>
  <div class="mt-5 flex gap-x-3">
    <a
      on:click={downloadKey}
      href={privateKeyDownload.href}
      download={privateKeyDownload.fileName}
      class="group relative flex-1 border border-neutral-300 bg-white p-4 text-left font-normal transition duration-300 hover:border-black"
    >
      <h6 class="text-sm font-medium">Private Key (Required)</h6>
      <p class="mt-2 text-xs leading-4">
        This private key will be lost after you close this dialog
      </p>
      <div class="mt-3.5 flex gap-x-1.5">
        <DownloadIcon
          class="h-4 w-4 text-neutral-500 transition duration-300 group-hover:text-black"
        />
        <small class="mt-px text-neutral-500 transition duration-300 group-hover:text-black">
          PEM format
        </small>
      </div>
    </a>
    <a
      href={csrDownload.href}
      download={csrDownload.fileName}
      class="group relative flex-1 border border-neutral-300 bg-white p-4 text-left font-normal transition duration-300 hover:border-black"
    >
      <h6 class="text-sm font-medium">Certificate Signing Request</h6>
      <p class="mt-2 text-xs leading-4">
        This signing request will be available for download later also
      </p>
      <div class="mt-3.5 flex gap-x-1.5">
        <DownloadIcon
          class="h-4 w-4 text-neutral-500 transition duration-300 group-hover:text-black"
        />
        <small class="mt-px text-neutral-500 transition duration-300 group-hover:text-black">
          CSR format
        </small>
      </div>
    </a>
  </div>
  <button
    on:click={showDetails}
    class="primary mt-5 w-full text-center !text-sm"
    disabled={!keyDownloaded}
  >
    Continue
  </button>
</main>
