<script>
  import { goto } from '$app/navigation'
  import { page } from '$app/stores'
  import { changeRole, deleteOne, getAllRoles } from '$lib/api/users.js'
  import TrashIcon from '~icons/tabler/trash'
  import SearchIcon from '~icons/tabler/search'
  import UserIcon from '~icons/tabler/user-shield'

  export let data
  let users = []

  let end = false
  let search = false
  let query = ''
  let timer

  let promise = getAllRoles()

  $: currentPage = Number($page.url.searchParams.get('page') ?? 1)

  $: updateUsers(data.users)

  const updateUsers = (newUsers) => {
    if (search) users = [...newUsers]
    else users = [...users, ...newUsers]

    end = newUsers.length < 4
  }

  const setQuery = (e) => {
    search = true
    clearTimeout(timer)

    const queryString = e.target.value?.trim() ?? ''

    if (query === queryString) {
      search = false
      return
    }

    query = queryString

    timer = setTimeout(async () => {
      const searchParams = createQuerySearchParam()
      await goto('/users?' + searchParams.toString(), {
        noScroll: true,
        replaceState: true,
        keepFocus: true
      })
    }, 250)
  }

  const onScroll = (e) => {
    search = false
    const searchParams = createQuerySearchParam()
    searchParams.append('page', currentPage + 1)
    goto('/users?' + searchParams.toString(), { noScroll: true })
  }

  const createQuerySearchParam = () => {
    const searchParams = new URLSearchParams()
    if (query.length > 0) {
      searchParams.append('query', query)
    }
    return searchParams
  }

  const deleteUser = async (id) => {
    try {
      await deleteOne(id)
      users = users.filter((u) => u.id != id)
    } catch (e) {
      console.log(e)
    }
  }

  const updateUser = async (id, event) => {
    try {
      await changeRole(id, event.target.value)
      users.find((user) => user.id === id).role = event.target.value
    } catch (e) {
      console.log(e)
    }
  }
</script>

<div class="mb-6 flex items-center justify-between">
  <div>
    <h1 class="text-xl">Users</h1>
    <p class="text-sm">Click on a CSR below to see details</p>
  </div>
  <div class="relative">
    <SearchIcon class="absolute left-4 top-0 mt-1.5 translate-y-1/2 text-[13px] text-gray-500" />
    <input
      on:input={setQuery}
      autocomplete="off"
      type="search"
      class=" bg-white pl-11"
      name="query"
      placeholder="Search users.."
    />
  </div>
</div>

<div class="mt-6 flex flex-col gap-4">
  {#each users as user}
    <div class="flex w-full items-center justify-between border border-neutral-300 bg-white p-5">
      <div>
        <p class="text-md font-semibold text-black">{user.firstName + ' ' + user.lastName}</p>
        <p class="text-sm text-neutral-600">
          {user.email}
        </p>
      </div>
      <div class="flex items-center">
        {#await promise}
          <p>Loading roles...</p>
        {:then roles}
        <div class="relative">
          <div class="absolute h-full flex items-center">
            <UserIcon class="text-[13px] ml-3 text-gray-700" />
          </div>
          <select
          on:change={(event) => updateUser(user.id, event)}
          class="h-10 pr-[58px] cursor-pointer text-[13px] pl-9"
          >
          {#each roles as role}
              <option value={role} selected={role === user.role}>
                {role[0].toUpperCase() + role.slice(1)}
              </option>
              {/each}
            </select>
          </div>
        {/await}
        <button
          class="h-10 px-8 cursor-pointer border border-l-0 border-neutral-300 bg-neutral-100 py-0 text-[13px]"
          on:click={() => deleteUser(user.id)}
        >
          <TrashIcon class="text-[13px] text-gray-700" />
        </button>
      </div>
    </div>
  {/each}
</div>

<div class="mt-2 text-sm font-normal text-neutral-600">
  {#if end}
    <p class="py-3 text-center">No more users to show</p>
  {:else}
    <button on:click={onScroll} class="mx-auto flex bg-transparent text-sm font-normal">
      Load More
    </button>
  {/if}
</div>
