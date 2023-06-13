<script>
  import { setTenants } from '$lib/api/buildings'
  import { getAll } from '$lib/api/users'
  import SearchIcon from '~icons/tabler/search'
  import XIcon from '~icons/tabler/x'
  import { user } from '$lib/stores/userStore'

  export let buildingId
  export let tenants
  export let close

  let foundUsers = []
  let query = ''
  let timer
  let loading = false

  let dropdownOpen = false
  $: dropdownOpen = query.length > 0
  $: !dropdownOpen && query.length === 0 && (foundUsers = [])

  const setQuery = async (e) => {
    clearTimeout(timer)
    const queryString = e.target.value?.trim() ?? ''
    timer = setTimeout(async () => {
      query = queryString
      if (query.length === 0) return (foundUsers = [])
      loading = true
      query && (await searchUsers(query))
      loading = false
    }, 500)
  }

  const searchUsers = async (query) => {
    try {
      const newUsers = await getAll(1, query)
      foundUsers = newUsers.filter(
        (u) =>
          u.id !== $user.id &&
          tenants.every((u1) => {
            return u1.id !== u.id
          })
      )
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

  const openDropdown = () => {
    query && (dropdownOpen = true)
  }

</script>

{#if dropdownOpen}
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div on:click={() => dropdownOpen = false} class="fixed top-0 left-0 z-[2] h-screen w-screen" />
{/if}
<form on:submit|preventDefault={saveChanges} class="flex w-[400px] flex-col justify-between">
  <div>
    <h2>Tenants</h2>
    <p class="text-neutral-600">Add or remove tenants from the building</p>
    <div class="relative z-[3] mt-6">
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
          on:focus={openDropdown}
          on:keydown={(e) => {
            if (e.key === 'Escape') {
              dropdownOpen = false
            }
          }}
        />
      </div>
      {#if dropdownOpen}
        <div
          class="absolute top-[45px] h-fit w-full divide-y-[1px] border border-t-0 border-neutral-300 bg-white shadow-lg"
        >
          {#if foundUsers.length === 0 && query.length > 0 && !loading}
            <div class="w-full p-3">
              <p class="text-center text-sm">No users found</p>
            </div>
          {:else}
            {#each foundUsers as user}
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              <div
                class="w-full cursor-pointer p-3 transition-colors hover:bg-neutral-50"
                on:click={() => addTenant(user)}
              >
                <p class="font-medium leading-5 text-black">{user.firstName} {user.lastName}</p>
                <p class="text-sm text-neutral-500">{user.email}</p>
              </div>
            {/each}
          {/if}
        </div>
      {/if}
    </div>

    <div class="mt-3 flex h-[400px] flex-col overflow-y-auto">
      {#if tenants.length === 0}
        <p class="text-neutral-500">No tenants added</p>
      {/if}
      {#each tenants as tenant}
        <div
          class="flex w-full justify-between border border-t-0 border-neutral-300 p-4 first:border-t"
        >
          <div class="flex gap-3">
            <div
              class=" flex aspect-square h-full items-center justify-center bg-neutral-200 text-black"
            >
              {tenant.firstName[0] + tenant.lastName[0]}
            </div>
            <div>
              <p class="font-medium leading-5 text-black">{tenant.firstName} {tenant.lastName}</p>
              <p class="text-sm text-neutral-500">{tenant.email}</p>
            </div>
          </div>
          <button type="button" on:click={() => removeTenant(tenant.id)} class="bg-transparent p-2">
            <XIcon class="text-xs text-neutral-500" />
          </button>
        </div>
      {/each}
    </div>
  </div>

  <div class="flex w-full justify-end">
    <div class="mt-6 flex gap-2">
      <button type="button" on:click={close} class="secondary !border-transparent"> Cancel </button>
      <button class="primary px-7">Save changes</button>
    </div>
  </div>
</form>
