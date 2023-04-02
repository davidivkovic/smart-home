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

<dialog bind:this={dialog} class="min-w-[300px] p-6 backdrop:bg-black/40 relative">
  {#if hasCloseButton}
    <button class="absolute top-5 right-0 bg-transparent" on:click={close}>
      <XIcon/>
    </button>
  {/if}
  <slot/>
</dialog>
