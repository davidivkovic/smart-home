<script>
  import { addBuilding } from '$lib/api/buildings'
  import BuildingTypePicker from './BuildingTypePicker.svelte'

  export let buildingTypes
  export let close
  let error = ''

  const saveBuilding = async (event) => {
    const data = Object.fromEntries(new FormData(event.target))
    const { name, address, type } = data
    try {
      await addBuilding(name.trim(), address.trim(), type)
      close()
    } catch (err) {
      error = err
    }
  }
</script>

<form class="w-[450px]" on:submit|preventDefault={saveBuilding}>
  <h2 class="text-xl">Add a building</h2>
  <p class="text-sm">Fill in the data about the building you want to monitor</p>
  <div class="flex gap-3">
    <div class="mt-5 flex flex-col gap-1">
      <label for="name">Name</label>
      <input
        required
        type="search"
        id="name"
        name="name"
        placeholder="Building name"
        autocomplete="off"
        maxlength="100"
      />
    </div>
    <div class="mt-5 flex flex-col gap-1">
      <label for="address">Address</label>
      <input
        required
        type="text"
        id="address"
        name="address"
        placeholder="Building address"
        autocomplete="address-level1"
        maxlength="100"
      />
    </div>
  </div>
  <div class="mt-5 flex flex-col gap-1">
    <label for="email" class="mb-2">
      <span>
        Category
      </span>
      <span class="block text-xs text-gray-500">
        Select the category that most suits your building
      </span>
    </label>
    <BuildingTypePicker types={buildingTypes} />
  </div>
  <p class="mt-2 text-center text-sm text-red-600">{error}</p>
  <button class="primary mt-6 flex w-full items-center justify-center gap-x-2 !py-2.5 !text-sm">
    Add building
  </button>
</form>
