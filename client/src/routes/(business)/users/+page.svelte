<script>
  import { goto, invalidate, invalidateAll } from '$app/navigation'
  import { page } from '$app/stores'
  import { changeRole, deleteOne, getAllRoles } from '$lib/api/users.js'
  import TrashIcon from '~icons/tabler/trash'

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
      users.find(user => user.id === id).role = event.target.value
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
  <div class="flex flex-col">
    <input
      on:input={setQuery}
      autocomplete="off"
      type="search"
      class="w-64 bg-white"
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
      <div class="flex items-center gap-2">
        {#await promise}
          <p>Loading roles...</p>
        {:then roles}
          <select on:change={(event) => updateUser(user.id, event)} class="w-28">
            {#each roles as role}
              <option value={role} selected={role === user.role}>{role}</option>
            {/each}
          </select>
        {/await}
        <button
          class="cursor-pointer bg-transparent text-[13px]"
          on:click={() => deleteUser(user.id)}
        >
          <TrashIcon class="text-[14px]" />
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
