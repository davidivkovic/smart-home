<script>
  import { onDestroy } from 'svelte'
  import dayjs from 'dayjs'
  import Log from '$lib/components/logs/Log.svelte'
  import { getAll } from '$lib/api/logs'
  import { shellHeight } from '$lib/stores/appStore'

  let logInstance
  let logs = []
  let logLevel = 'ALL'
  let regex = ''
  let isFetching = false
  let exhausted = false
  let initial = true
  let before = dayjs()
  let after = null

  const addLogs = async (before, after, logLevel, regex) => {
    const data = await getAll(before, after, logLevel, regex)
    logs = [...data]
  }

  $: if (regex || logLevel) {
    before = dayjs()
    after = null
    exhausted = false
  }

  $: addLogs(before, after, logLevel, regex)
  

  $: if (logs.length && logInstance && initial) {
    setTimeout(logInstance.scrollToBottom, 0)
    initial = false
  }

  const onScroll = async ({
      target: { scrollTop, clientHeight, scrollHeight },
    }) => {
      if (
        clientHeight - scrollTop + 10 >= scrollHeight &&
        !isFetching &&
        !exhausted
      ) {
        isFetching = true
        const olderLogs = await getAll(logs.at(-1).timestamp, null, logLevel, regex)
        exhausted = olderLogs.length == 0

        if (!exhausted) {
          logs = [...logs, ...olderLogs]
        }

        isFetching = false
      }
    }

    const interval = setInterval(async () => {
      const newerLogs = await getAll(null, logs.at(0).timestamp, logLevel, regex)
      if (newerLogs.length) {
        logs = [...newerLogs, ...logs]
      }
    }, 5000)

    onDestroy(() => clearInterval(interval))

</script>

<div class="flex flex-col relative" style="height: calc(100vh - {$shellHeight}px);">
  <div class="flex items-start">
    <div class="">
      <div class="flex items-center">
        <h1 class="text-xl">Service Logs</h1>
        <span class="relative flex h-4 w-10 mt-0.5 ml-2">
          <span class="animate-ping absolute inline-flex rounded ml-1.5 mt-0.5 h-3 w-7 opacity-75 bg-red-400"></span>
          <span class="relative inline-flex rounded-sm h-4 w-12 bg-red-500"></span>
          <span class="inset-x-auto absolute text-xs text-white font-medium w-10 flex justify-center">LIVE</span>
        </span>
      </div>
      <p class="text-sm">View and search for logs from all platform services</p>
    </div>

    <div class="ml-auto mr-2 -mt-3">
      <label for="log-level-select" class="text-[13px] font-semibold block pb-0.5">Log Level</label>
      <select 
        id="log-level-select" 
        bind:value={logLevel} 
        class="bg-white cursor-pointer w-32 font-medium"
      >
        <option>ALL</option>
        <option>INFO</option>
        <option>WARN</option>
        <option>ERROR</option>
      </select>
    </div>
    <div class="-mt-3">
      <label for="log-search-input" class="text-[13px] font-semibold block pb-0.5">Search</label>
      <input 
        id="log-search-input" 
        autocorrect="off"
        type="search" 
        placeholder="Regex pattern (.*)" 
        class="bg-white w-64"
        bind:value={regex}
      />
    </div>
  </div>
  <Log logs={logs} bind:this={logInstance} class="mt-4 flex-1 -mb-6" on:scroll={e => onScroll(e.detail)} />
</div>