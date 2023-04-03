import { writable, get } from 'svelte/store'

export const dialogs = writable([])
export const notifications = writable([])

export const openDialog = (dialogComponent, props = {}, onClose = (value = '') => {}) => {
  const id = get(dialogs).length + 1
  const dialog = {
    id,
    props,
    content: dialogComponent,
    hasCloseButton: props.hasCloseButton ?? true,
    close: (value) => {
      onClose(value)
      closeDialog(id)
    }
  }
  dialogs.update((d) => [...d, dialog])
}

export const closeDialog = (id) => {
  dialogs.update((d) => d.filter((dialog) => dialog.id !== id))
}

export const openNotification = (text, title = '') => {
  const id = get(notifications).length + 1
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
