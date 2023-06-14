<script>
  import dayjs from 'dayjs'
  import Log from '$lib/components/logs/Log.svelte'
  import { getAll } from '$lib/api/logs'
  import { openNotification, shellHeight } from '$lib/stores/appStore'

  let logInstance
  let logs = []
  let logLevel
  let regex = ''

  $: {
    getAll(dayjs().add(2, 'h'), null, logLevel, regex).then((data) => {
      logs = [...data]
    })
  }

</script>

<div class="flex flex-col" style="height: calc(100vh - {$shellHeight}px);">
  <div class="flex items-end">
    <div class="">
      <h1 class="text-xl">Service Logs</h1>
      <p class="text-sm">View and search for logs from all platform services</p>
    </div>
    <div class="ml-auto mr-2">
      <label for="log-level-select" class="text-[13px] block pb-0.5">Log Level</label>
      <select 
        id="log-level-select" 
        bind:value={logLevel} 
        class="bg-white cursor-pointer w-32 font-medium"
      >
        <option selected>ALL</option>
        <option>INFO</option>
        <option>WARN</option>
        <option>ERROR</option>
      </select>
    </div>
    <div>
      <label for="log-search-input" class="text-[13px] block pb-0.5">Search</label>
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
  <Log logs={logs} bind:this={logInstance} class="mt-4 flex-1 -mb-6" />
</div>