<script>
  import { getDeviceTypes } from '$lib/api/devices'
  import { getBuildingTypes } from '$lib/api/buildings'
</script>

<div>
  <h3>Device Types</h3>
  {#await getDeviceTypes()}
    <p>...loading</p>
  {:then types}
    <ul>
      {#each types as devices}
        <h6>{devices[0].categoryName}</h6>
        <div class="grid grid-cols-2">
          {#each devices as device}
          <!-- <li>{JSON.stringify(device)}</li> -->
            <div class="flex items-center">
              <img src={device.image} alt={device.name} class="h-16 w-16" />
              <p>{device.name}</p>
            </div>
          {/each}
        </div>
      {/each}
    </ul>
  {:catch error}
    <p>{error.message}</p>
  {/await}

  <h3>Building Types</h3>
  {#await getBuildingTypes()}
    <p>...loading</p>
  {:then types}
    <ul>
      {#each types as building}
        <li>{JSON.stringify(building)}</li>
        <img src={building.image} alt={building.name} class="h-16 w-16" />
      {/each}
    </ul>
  {:catch error}
    <p>{error.message}</p>
  {/await}

</div>