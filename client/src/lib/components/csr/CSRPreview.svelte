<script>
  import dayjs from 'dayjs'
  import ClockIcon from '~icons/tabler/clock-filled'
  import CheckIcon from '~icons/tabler/circle-check-filled'
  import XIcon from '~icons/tabler/circle-x-filled'
  import { openDialog } from '$lib/stores/appStore'
  import CsrDetailsDialog from './CSRDetailsDialog.svelte'

  export let csr

  const statusColor =
    csr.status === 'Pending' ? 'amber' : csr.status === 'Approved' ? 'green' : 'red'

  const openDetails = () => {
    openDialog(CsrDetailsDialog, { csr, hasCloseButton: false })
  }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<div
  on:click={openDetails}
  class="w-full cursor-pointer border bg-white border-neutral-300 p-5 transition duration-300 hover:border-black"
>
  <div class="flex justify-between">
    <p class="text-md font-semibold text-black">{csr.commonName}</p>
    <div class="flex items-center gap-1.5">
      {#if csr.status === 'Pending'}
        <ClockIcon class="h-4 w-4 text-amber-500" />
      {:else if csr.status === 'Approved'}
        <CheckIcon class="h-4 w-4 text-green-500" />
      {:else}
        <XIcon class="h-4 w-4 text-red-500" />
      {/if}
      <span class="text-sm  {`text-${statusColor}-600`}">{csr.status}</span>
    </div>
  </div>
  <p class="text-sm text-neutral-600">
    Requested at {dayjs(csr.requestedAt).format('MMMM D, YYYY mm:HH')}
  </p>
</div>
