<script>
  import { openDialog } from '$lib/stores/appStore'
  import { getBuildingTypes } from '$lib/api/buildings'
  import AddBuildingDialog from '$lib/components/buildings/AddBuildingDialog.svelte'
  import { invalidateAll } from '$app/navigation'

  export let data
  let buildingTypes

  const addBuilding = async () => {
    openDialog(AddBuildingDialog, { buildingTypes }, () => invalidateAll())
  }

  const preloadTypes = async () => {
    if (!buildingTypes) {
      buildingTypes = await getBuildingTypes()
    }
  }
</script>

<h1 class="text-xl">Buildings page</h1>
<p class="text-sm">View and manage your buildings</p>
<button on:mouseenter={preloadTypes} on:click={addBuilding} class="primary mt-4"
  >Add a building</button
>

{#each data.buildings as building}
  <p>{building.name}</p>
{/each}
