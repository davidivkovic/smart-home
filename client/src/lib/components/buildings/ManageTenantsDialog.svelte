<script>
  import { setTenants } from '$lib/api/buildings'
  import { getAll } from '$lib/api/users'
  import SearchIcon from '~icons/tabler/search'
  import TrashIcon from '~icons/tabler/trash'

  export let buildingId
  export let tenants
  export let close

  let foundUsers = []
  let query = ''
  let timer

  let dropdownOpen = false
  $: dropdownOpen = query.length > 0

  const setQuery = (e) => {
    clearTimeout(timer)
    const queryString = e.target.value?.trim() ?? ''
    timer = setTimeout(async () => {
      query = queryString
      query && searchUsers(query)
    }, 300)
  }

  const searchUsers = async (query) => {
    try {
      foundUsers = await getAll(1, query)
    } catch (err) {
      console.log(err)
    }
  }

  const addTenant = async (user) => {
    if (tenants.find((tenant) => tenant.id === user.id)) return (dropdownOpen = false)
    tenants = [...tenants, user]
    query = ''
    dropdownOpen = false
  }

  const removeTenant = (id) => {
    tenants = tenants.filter((tenant) => tenant.id !== id)
  }

  const saveChanges = async () => {
    console.log('saving')
    try {
      await setTenants(
        buildingId,
        tenants.map((tenant) => tenant.id)
      )
      close()
    } catch (err) {
      console.log(err)
    }
  }
</script>

<form on:submit|preventDefault={saveChanges} class="flex w-[500px] flex-col justify-between">
  <div>
    <h2>Tenants</h2>
    <p class="text-neutral-600">Add or remove tenants from the building</p>
    <div class="relative mt-6">
      <div class=" w-full">
        <SearchIcon
          class="absolute left-4 top-0 mt-1.5 translate-y-1/2 text-[13px] text-gray-500"
        />
        <input
          on:input={setQuery}
          autocomplete="off"
          type="search"
          class=" w-full bg-white pl-11"
          name="query"
          placeholder="Search users.."
          maxlength="50"
          value={query}
        />
      </div>
      {#if dropdownOpen}
        <div
          class="absolute top-[45px] h-fit w-full divide-y-[1px] border border-t-0 border-neutral-300 bg-white"
        >
          {#if foundUsers.length === 0 && query.length > 0}
            <!-- <div class="w-full p-3">
              <p class="text-center">No users found</p>
            </div> -->
          {:else}
            {#each foundUsers as user}
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              <div class="w-full cursor-pointer p-3" on:click={() => addTenant(user)}>
                <p>{user.firstName} {user.lastName}</p>
                <p class="text-sm text-neutral-500">{user.email}</p>
              </div>
            {/each}
          {/if}
        </div>
      {/if}
    </div>

    <div class="mt-3 flex h-[400px] flex-col gap-3 overflow-y-auto">
      {#if tenants.length === 0}
        <p class="text-neutral-500">No tenants added</p>
      {/if}
      {#each tenants as tenant}
        <div class="flex w-full justify-between border border-neutral-300 p-4">
          <div>
            <p>{tenant.firstName} {tenant.lastName}</p>
            <p class="text-sm text-neutral-500">{tenant.email}</p>
          </div>
          <button on:click={() => removeTenant(tenant.id)} class="bg-transparent p-2">
            <TrashIcon class="text-neutral-500" />
          </button>
        </div>
      {/each}
    </div>
  </div>

  <div class="w-full flex justify-end">
    <div class="mt-6 flex gap-2">
      <button type="button" on:click={close} class="secondary !border-transparent"> Cancel </button>
      <button class="primary px-7">Save changes</button>
    </div>
  </div>
</form>
