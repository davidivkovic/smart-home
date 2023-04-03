<script>
  import dayjs from 'dayjs'
  import ClockIcon from '~icons/tabler/clock-filled'
  import CheckIcon from '~icons/tabler/circle-check-filled'
  import XIcon from '~icons/tabler/circle-x-filled'
  import { openDialog } from '$lib/stores/appStore'

  export let csr

  const statusColor =
    csr.status === 'Pending' ? 'amber' : csr.status === 'Approved' ? 'green' : 'red'

  const openDetails = async () => {
    openDialog(
      await import('./CSRDetailsDialog.svelte').then($ => $.default), 
      { csr, hasCloseButton: true },
      (value) => {
        if (value === 'signed') {
          csr.status = 'Approved'
        } else if (value === 'rejected') {
          csr.status = 'Rejected'
        }
      }
    )
  }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<div
  on:click={openDetails}
  class="w-full cursor-pointer border border-neutral-300 bg-white p-5 transition duration-300 hover:border-black"
>
  <div class="flex justify-between items-center">
    <div>
      <p class="text-md font-semibold text-black">{csr.commonName}</p>
      <p class="text-sm text-neutral-600">
        Requested on {dayjs(csr.requestedAt).format('MMMM D, YYYY [at] HH:mm')}
      </p>
    </div>
    <div class="flex items-center gap-1.5">
      <span class="text-[13px] font-medium">{csr.status}</span>
      {#if csr.status === 'Pending'}
        <ClockIcon class="h-4 w-4 text-amber-500" />
      {:else if csr.status === 'Approved'}
        <CheckIcon class="h-4 w-4" />
      {:else}
        <XIcon class="h-4 w-4 text-red-500" />
      {/if}
    </div>
  </div>
</div>
