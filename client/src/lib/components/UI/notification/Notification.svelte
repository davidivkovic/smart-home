<script>
  import XIcon from '~icons/tabler/x'
  export let title
  export let text
  export let close

</script>

<div class="mx-auto w-[900px] bg-white border border-zinc-300 p-6 mt-6">
  <div class="flex items-center justify-between">
    <div>
      <p class="font-bold">{title}</p>
      <p>{text}</p>
    </div>
    <button on:click={close} class="bg-white">
      <XIcon />
    </button>
  </div>
</div>
