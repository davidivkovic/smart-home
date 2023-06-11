<script>
  export let types

  const descriptions = {
    'APARTMENT': 'Single apartment',
    'HOUSE': 'Residential building',
    'BUSINESS': 'Commercial building',
    'OTHER': 'Other buildings',
  }
</script>

<div class="grid grid-cols-2 grid-rows-2 gap-3">
  {#each types as type}
    <div class="h-full w-full">
      <input
        type="radio"
        id={type.category}
        required
        name="type"
        class="peer absolute opacity-0"
        value={type.category}
      />
      <label
        for={type.category}
        class="relative block w-full cursor-pointer border border-neutral-300  peer-checked:outline peer-checked:outline-black"
      >
        <div class="flex h-fit w-full items-center gap-4 px-4 py-5">
          <img width="40" height="40" src={type.image} class="grayscale brightness-105" alt={`type.categoryName`} />
          <div>
            <p class="text-sm text-black">{type.categoryName}</p>
            <p class="text-xs text-neutral-500">{descriptions[type.category]}</p>
          </div>
        </div>
      </label>
    </div>
  {/each}
</div>
