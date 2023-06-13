<script>
  export let deviceTypes
  export let buildingId

  const brands = ['Huawei', 'Xiaomi', 'Samsung']

  const saveDevice = (event) => {
    event.preventDefault()
    const data = Object.fromEntries(new FormData(event.target))
    console.log(data, buildingId)
  }

</script>

<form on:submit|preventDefault={saveDevice} class="max-h-[90vh] w-[600px]">
  <h1 class="text-2xl">Add a device</h1>
  <p class="text-sm">Fill in the data about the device you want to monitor</p>

  <div class="mt-5">
    <div class="flex gap-3">
      <div class="flex flex-1 flex-col gap-1">
        <label for="name">Name</label>
        <input
          required
          type="search"
          id="name"
          name="name"
          placeholder="Device name"
          autocomplete="off"
          maxlength="100"
        />
      </div>
      <div class="flex flex-1 flex-col gap-1">
        <label for="brand">Brand</label>
        <select id="brand" name="brand">
          {#each brands as brand}
            <option value={brand}>{brand}</option>
          {/each}
        </select>
      </div>
    </div>
  </div>
  <div class="mt-5">
    <label class="  text-black" for="category">Device category</label>
    <p class="text-sm">
      Pick a category that suits your device the best Fill in the data about the building you want
      to monitor
    </p>
  </div>
  {#each deviceTypes as category}
    <div class="mt-4">
      <h2 class="text-sm font-medium">{category[0].categoryName}</h2>
      <div class="mt-2 grid grid-cols-2 gap-2">
        {#each category as type}
          <div>
            <input
              required
              type="radio"
              name="device"
              value={type.id}
              id={type.id}
              class="peer absolute block opacity-0"
            />
            <label
              for={type.id}
              class=" flex cursor-pointer items-center gap-x-2 border border-neutral-300 bg-white p-3 transition hover:border-black peer-checked:border-black"
            >
              <img alt={type.name} src={type.image} class="h-10 w-10 object-contain" />
              <p class="text-sm">{type.name}</p>
            </label>
          </div>
        {/each}
      </div>
    </div>
  {/each}

  <div class="py-4">
    <button class="primary w-full !text-sm">Add device</button>
  </div>
</form>
