<script>
  import { onMount } from 'svelte'

  import XIcon from '~icons/tabler/x'

  export let close
  let dialog

  onMount(() => {
    dialog.showModal()
  })

</script>

<dialog bind:this={dialog} class="min-w-[300px] rounded-lg p-6 backdrop:bg-black/40 relative">
  <button class="absolute top-5 right-0" on:click={close}><XIcon/></button>
  <slot/>
</dialog>
