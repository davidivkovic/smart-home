<script>
  import { getAll } from '$lib/api/users'
  import SearchIcon from '~icons/tabler/search'
  import UserIcon from '~icons/tabler/user-shield'

  export let buildingId
  export let tenants

  let foundUsers = []
  let query = ''
  let timer

  const setQuery = (e) => {
    clearTimeout(timer)
    const queryString = e.target.value?.trim() ?? ''
    timer = setTimeout(async () => {
      query = queryString
      searchUsers(query)
    }, 250)
  }

  const searchUsers = async (query) => {
    try {
      foundUsers = await getAll(1, query)
    } catch (err) {
      console.log(err)
    }
  }

</script>

<div class="min-h-[600px] w-[500px]">
  <h2>Tenants</h2>
  <p class="text-neutral-600">Add or remove tenants from the building</p>
  <div class="relative mt-6">
    <div class=" w-full">
      <SearchIcon class="absolute left-4 top-0 mt-1.5 translate-y-1/2 text-[13px] text-gray-500" />
      <input
        on:input={setQuery}
        autocomplete="off"
        type="search"
        class=" w-full bg-white pl-11"
        name="query"
        placeholder="Search users.."
        maxlength="50"
      />
    </div>
  </div>

  {#each foundUsers as user}

  <p>{user.email}</p>
  {/each}

  {#if tenants.length === 0}
    <p class="py-3 text-center">No tenants</p>
  {/if}

  {#each tenants as tenant}
    <p>{tenant.fullName}</p>
  {/each}
</div>
