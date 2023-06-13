<script>
  import { invalidateAll } from '$app/navigation'

  import { isLandlord, user } from '$lib/stores/userStore'
  import { openDialog } from '$lib/stores/appStore'
  import { getDeviceTypes } from '$lib/api/devices'
  import { deleteBuilding, getBuildingTypes } from '$lib/api/buildings'
  import AddBuildingDialog from '$lib/components/buildings/AddBuildingDialog.svelte'
  import ManageTenantsDialog from '$lib/components/buildings/ManageTenantsDialog.svelte'

  import TrashIcon from '~icons/tabler/trash'
  import AddDeviceDialog from '$lib/components/devices/AddDeviceDialog.svelte'
  import { building } from '$app/environment'

  export let data
  let buildingTypes
  let deviceTypes

  const addBuilding = async () => {
    openDialog(AddBuildingDialog, { buildingTypes }, () => invalidateAll())
  }

  const preloadTypes = async () => {
    if (!buildingTypes) {
      buildingTypes = await getBuildingTypes()
    }
  }

  const preloadDeviceTypes = async () => {
    if (!deviceTypes) {
      deviceTypes = await getDeviceTypes()
    }
  }

  const remove = async (id) => {
    try {
      await deleteBuilding(id)
      data.buildings = data.buildings.filter((building) => building.id !== id)
    } catch (err) {
      console.log(err)
    }
  }

  const manageTenants = async (building) => {
    openDialog(ManageTenantsDialog, { buildingId: building.id, tenants: building.tenants }, () =>
      invalidateAll()
    )
  }

  const addDevice = async () => {
    openDialog(AddDeviceDialog, { buildingId: building.id, deviceTypes  }, () => invalidateAll())
  }
</script>

<div class="flex">
  <div>
    <h1 class="text-xl">Buildings</h1>
    <p class="text-sm">View and manage your buildings</p>
  </div>
  {#if $isLandlord}
    <button
      on:mouseenter={preloadTypes}
      on:click={addBuilding}
      class="secondary ml-auto mt-1 h-11 !border-neutral-300 py-1 px-10 !text-sm"
      >Add a building</button
    >
  {/if}
</div>

{#if data.buildings.length === 0}
  <p class="py-3 text-center">No buildings</p>
{/if}

<div class="mt-6 flex flex-col gap-4">
  {#each data.buildings as building}
    <div class="flex w-full items-center justify-between border border-neutral-300 bg-white p-5">
      <div class="flex items-center gap-4">
        <img src={building.type.image} class="h-16 w-16" alt="Building type" />
        <div>
          <p class="font-medium text-black">{building.name}</p>
          <p class="text-sm text-neutral-600">{building.type.categoryName} at {building.address}</p>
        </div>
      </div>
      <div class="flex">
        <button
          class="h-10 cursor-pointer border border-neutral-300 bg-neutral-100 px-6 py-0 text-[13px]"
          on:click={() => manageTenants(building)}
        >
          Tenants
        </button>
        <button
          class="h-10 cursor-pointer border border-l-0 border-neutral-300 bg-neutral-100 px-6 py-0 text-[13px]"
          on:click={() => remove(building.id)}
        >
          <TrashIcon class="text-[13px] text-gray-700" />
        </button>
      </div>
    </div>
    <div>
      <button on:mouseenter={preloadDeviceTypes} on:click={addDevice}>Devices</button>
    </div>
  {/each}
</div>
