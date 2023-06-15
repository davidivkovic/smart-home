<script>
  import { invalidateAll } from '$app/navigation'

  import { isLandlord, user } from '$lib/stores/userStore'
  import { openDialog } from '$lib/stores/appStore'
  import { getDeviceTypes } from '$lib/api/devices'
  import { deleteBuilding, getBuildingTypes } from '$lib/api/buildings'
  import AddBuildingDialog from '$lib/components/buildings/AddBuildingDialog.svelte'
  import ManageTenantsDialog from '$lib/components/buildings/ManageTenantsDialog.svelte'

  import TrashIcon from '~icons/tabler/trash'
  import PlusIcon from '~icons/tabler/plus'
  import AddDeviceDialog from '$lib/components/devices/AddDeviceDialog.svelte'

  export let data

  const addBuilding = async () => {
    const buildingTypes = await getBuildingTypes()
    openDialog(AddBuildingDialog, { buildingTypes }, () => invalidateAll())
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

  const addDevice = async (building) => {
    const deviceTypes = await getDeviceTypes()
    openDialog(AddDeviceDialog, { buildingId: building.id, deviceTypes }, () => invalidateAll())
  }
</script>

<div class="flex">
  <div>
    <h1 class="text-xl">Buildings</h1>
    <p class="text-sm">View and manage your buildings</p>
  </div>
  {#if $isLandlord}
    <button
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
    <div class="border border-neutral-300 bg-white">
      <div class=" flex w-full items-center justify-between border-neutral-300 p-5">
        <div class="flex items-center gap-4">
          <img src={building.type.image} class="h-16 w-16" alt="Building type" />
          <div>
            <p class="font-medium text-black">{building.name}</p>
            <p class="text-sm text-neutral-600">
              {building.type.categoryName} at {building.address}
            </p>
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
      <div class="-mt-2 px-5 pb-5">
        <div class="mt-4 grid grid-flow-row auto-rows-max grid-cols-5 gap-3">
          {#each building.devices as device}
            <div
              class="flex h-36 cursor-pointer flex-col items-center justify-center border bg-neutral-100 p-4 text-center transition-colors hover:bg-neutral-200"
            >
              <span class="text-xs text-neutral-600">{device.brand}</span>
              <img src={device.type.image} class="h-12 w-12" alt="Device type" />
              <p class="mt-2 text-sm font-medium text-black">{device.name}</p>
              <span class="w-full truncate text-xs text-neutral-600">{device.type.name}</span>
            </div>
          {/each}
          <button
            on:click={() => addDevice(building)}
            class="flex h-36 flex-col items-center justify-center gap-2 border bg-neutral-100 hover:bg-neutral-200"
          >
            <!-- <PlusIcon class="w-8 h-8 text-neutral-600 stroke-1" /> -->
            <svg
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="h-7 w-7 text-neutral-600"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>

            <span class="text-[13px] text-neutral-600">Add device</span>
          </button>
        </div>
      </div>
    </div>
  {/each}
</div>
