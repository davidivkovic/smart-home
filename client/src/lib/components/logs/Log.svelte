<script>
  import { createEventDispatcher } from 'svelte'
  import ArrowDownIcon from '~icons/tabler/arrow-down'

  const dispatch = createEventDispatcher()

  let scrollPanel

  const logLevels = {
    INFO: {
      text: 'text-[#d928fc]',
      shadow: 'text-shadow: 1px 1px 24px #e156fc;',
      divide: 'divide-[#e156fc]'
    },
    WARN: {
      text: 'text-[#fca728]',
      shadow: 'text-shadow: 1px 1px 24px #fcbf28;',
      divide: 'divide-[#fcbf28]'
    },
    ERROR: {
      text: 'text-[#fc2828]',
      shadow: 'text-shadow: 1px 1px 24px #fc2828bf;',
      divide: 'divide-[#fc2828]'
    }
  }

  export let logs = []
  export function scrollToBottom() {
    scrollPanel.scrollTo({ top: scrollPanel.scrollHeight, behavior: 'smooth' })
  }

</script>

<div 
  bind:this={scrollPanel}
  on:scroll={e => dispatch('scroll', e)}
  class="flex flex-col-reverse bg-white border border-neutral-200 text-[13px] md:text-sm py-2 overflow-y-auto overscroll-contain {$$props.class}"
>
  {#each logs as log, index}
    <div class="flex px-2 divide-x py-1 first:mb-auto">
      <code class="basis-14 pl-2 py-px text-neutral-500 text-[13px]">
        <!-- { dayjs(log.timestamp).format('DD/MM/YYYY HH:mm') } -->
        { index + 1 }
      </code>
      <code class="flex-1 min-w-0 pl-4 py-px break-words">
        <code 
          class="font-bold {logLevels[log.level].text}" 
          style="{logLevels[log.level].shadow}"
        >
          { log.level }
        </code> 
        - { log.message }
      </code>
    </div>
  {/each}
</div>
<button on:click={scrollToBottom} class="absolute flex items-center -bottom-3 right-6 !border-neutral-300 secondary px-3 py-2">
  <span class="text-[13px] mr-1">Show Newest</span>
  <ArrowDownIcon class="w-4 h-4" />
</button>