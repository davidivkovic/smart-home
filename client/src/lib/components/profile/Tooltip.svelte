<script>
  import { fade } from 'svelte/transition'
  import CheckIcon from '~icons/tabler/check'

  export let text
  export let open
</script>

{#if open}
  <div in:fade={{duration: 250}} out:fade={{duration: 250}}
    class="absolute -top-12 right-0 flex w-max select-none items-center gap-2 bg-black py-2 pl-3 pr-4 font-normal text-white
  after:absolute
  after:right-4 after:top-full
  after:border-l-[10px] after:border-r-[10px]
  after:border-t-[10px] after:border-l-transparent
  after:border-r-transparent after:border-t-black"
  >
    <CheckIcon class="text-xs" />
    <span class="text-[13px]">{text}</span>
  </div>
{/if}
