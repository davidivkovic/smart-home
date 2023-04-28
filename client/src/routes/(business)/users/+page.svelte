<script>
  import { goto, invalidate, invalidateAll } from '$app/navigation'
  import { page } from '$app/stores'
  import { changeRole, deleteOne } from '$lib/api/users.js'
  import TrashIcon from '~icons/tabler/trash'

  export let data
  let users = []
  let end = false
  let search = false
  let query = ''
  let timer

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
      await goto(
        '/users?' + new URLSearchParams({ query }).toString(), 
        { noScroll: true, replaceState: true, keepFocus: true }
      )
    }, 250)
  }

  const onScroll = (e) => {
    search = false
    goto(`/users?query=${e.target.value}&page=${currentPage + 1}`, { noScroll: true })
  }

  const deleteUser = async (id) => {
    await deleteOne(id)
    users = users.filter(u => u.id != id)
  }

  const updateUser = async (id, role) => {
    await changeRole(id, role)
    // TODO: Fix 
    invalidateAll()
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
      <div class="flex items-center gap-4">
        <div class="text-sm">
          <button
            on:click={() => updateUser(user.id, 'admin')}
            class={`bg-transparent p-0 ${
              user.role === 'admin' ? 'font-semibold' : 'cursor-pointer font-normal'
            }`}>admin</button
          >
          /
          <button
            on:click={() => updateUser(user.id, 'user')}
            class={`bg-transparent p-0 ${
              user.role === 'user' ? 'font-semibold' : 'cursor-pointer font-normal'
            }`}>user</button
          >
        </div>
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
