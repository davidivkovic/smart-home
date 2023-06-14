<script>
  import dayjs from 'dayjs'

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
  class="bg-white border border-neutral-200 rounded text-[13px] md:text-sm py-2 overflow-y-auto overscroll-contain {$$props.class}"
>
  <div class="flex flex-col-reverse">
    {#each logs as log, index}
      <div class="flex px-4 divide-x py-1">
        <code class="basis-16 pl-2 py-px text-neutral-500">{ logs.length - index }</code>
        <code class="flex-1 min-w-0 pl-4 py-px break-words">
          <code class="font-bold {logLevels[log.level].text}" style="{logLevels[log.level].shadow}">{ log.level }</code> 
          - { dayjs(log.timestamp).format('DD/MM/YYYY HH:mm') } - { log.message }
        </code>
      </div>
    {/each}
  </div>
</div>