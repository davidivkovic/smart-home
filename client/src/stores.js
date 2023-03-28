import { writable } from 'svelte/store'

export const dialogs = writable([])
export const notifications = writable([])

export const openDialog = (dialogComponent, props, onClose = () => {}) => {
  let currentCount
  const unsubscribe = dialogs.subscribe((d) => (currentCount = d.length))
  const id = currentCount + 1
  const dialog = {
    id,
    props,
    content: dialogComponent,
    close: () => {
      onClose()
      closeDialog(id)
    }
  }
  dialogs.update((d) => [...d, dialog])
  unsubscribe()
}

export const closeDialog = (id) => {
  dialogs.update((d) => d.filter((dialog) => dialog.id !== id))
}

export const openNotification = (text, title = '') => {
  let currentCount
  const unsubscribe = notifications.subscribe((n) => (currentCount = n.length))
  unsubscribe()
  const id = currentCount + 1
  notifications.update((n) => [
    ...n,
    {
      id,
      text,
      title,
      close: () => {
        closeNotification(id)
      }
    }
  ])
  console.log('id again', id)
}

export const closeNotification = (id) => {
  notifications.update((n) => n.filter((notification) => notification.id !== id))
}
