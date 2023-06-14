<script>
  import { onMount } from 'svelte'

  import XIcon from '~icons/tabler/x'

  export let close
  export let hasCloseButton = true

  let dialog

  onMount(() => {
    dialog.showModal()
  })
</script>

<dialog
  bind:this={dialog}
  class="relative min-w-[300px] overflow-x-hidden p-6 backdrop:bg-black/40"
>
  {#if hasCloseButton}
    <button class="sticky !-top-2 !-mt-10 !-mr-5 -right-5 z-10 float-right bg-transparent" on:click={close}>
      <XIcon />
    </button>
  {/if}
  <slot />
</dialog>
