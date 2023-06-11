<script>
  export let types
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
        class="relative block w-full cursor-pointer border border-neutral-300 bg-neutral-100 peer-checked:outline peer-checked:outline-black"
      >
        <div class="flex h-fit w-full items-center gap-4 p-3">
          <img width="64" height="64" src={type.image} class="h-16" alt={`type.categoryName`} />
          <span class="text-sm">{type.categoryName}</span>
        </div>
      </label>
    </div>
  {/each}
</div>
