<script>
  import { openDialog } from '$lib/stores/appStore'
  import { isAdmin } from '$lib/stores/userStore'
  import { getCA } from '$lib/api/certificates'
  import { rejectCSR } from '$lib/api/csrs'
  import { RDN } from '$lib/crypto/csr'
  import dayjs from 'dayjs'


  export let csr
  export let close

  let selectedFieldIndex = -1
  let csrFields = [
    {
      name: RDN.commonName,
      value: csr.commonName
    },
    {
      name: RDN.country,
      value: csr.country
    },
    {
      name: RDN.locality,
      value: csr.locality
    },
    {
      name: RDN.organization,
      value: csr.organization
    },
    {
      name: RDN.organizationalUnit,
      value: csr.organizationUnit
    },
    {
      name: RDN.state,
      value: csr.state
    },
    {
      name: RDN.email,
      value: csr.email
    }
  ].filter((field) => field.value)

  async function openSigningDialog() {
    const [CSRSigningDialog, caCertificates] = await Promise.allSettled([
      import('./CSRSigningDialog.svelte').then(($) => $.default),
      getCA()
    ])
    openDialog(
      CSRSigningDialog.value, 
      { caCertificatesPEM: caCertificates.value, csr }, 
      (result) => close(result)
    )
  }

  async function reject() {
    try {
      await rejectCSR(csr.id, '')
      close('rejected')
    }
    catch (error) {
      console.error(error)
    }
  }
</script>

<div class="w-[340px]">
  <h3>Certificate Signing Request</h3>
  <p class="text-sm text-neutral-600">
    Requested on {dayjs(csr.requestedAt).format('MMMM D, YYYY [at] HH:mm')}
  </p>
  <div class="mt-3 flex-1 border border-neutral-300">
    <h4 class="border-b border-neutral-300 bg-neutral-50 px-3 py-2 text-sm font-medium">
      Subject Data
    </h4>
    <table class="w-full">
      <tbody class="">
        {#each csrFields as field, index}
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
              <p class="ml-1.5 text-[13px]">{field.value}</p>
            </td>
          </tr>
          <!-- </div> -->
        {/each}
      </tbody>
    </table>
  </div>
  {#if csr.status === 'Pending' && $isAdmin}
    <div class="mt-4 flex gap-x-2">
      <button on:click={openSigningDialog} class="primary flex-1 !text-sm"
        >Sign Certificate Request</button
      >
      <button on:click={reject} class="secondary ml-auto !text-sm">Reject</button>
    </div>
  {/if}
</div>
