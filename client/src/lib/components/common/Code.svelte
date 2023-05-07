<script>
  const inputCount = 6
  const codeInputs = Array.from(Array(inputCount).keys())

  let inputContainer
  export let required = true

  const handleInput = (e) => {
    const input = e.target
    const keyCode = e.key
    const previousSibling = input.previousElementSibling
    const nextSibling = input.nextElementSibling

    if (keyCode === 'ArrowLeft' && previousSibling) {
      focusSibling(previousSibling)
    } else if (['ArrowRight', 'Tab'].includes(keyCode) && input.nextElementSibling) {
      focusSibling(nextSibling)
    } else if (keyCode === 'Backspace') {
      input.value = ''
      if (previousSibling) {
        focusSibling(previousSibling)
      }
    } else if (Number(keyCode) || keyCode === '0') {
      input.value = Number(keyCode)
      if (nextSibling) {
        focusSibling(nextSibling)
      }
    }
  }

  const handlePaste = (e) => {
    const paste = e.clipboardData.getData('text')
    const inputs = inputContainer.childNodes
    inputs.forEach((input, index) => (input.value = paste[index]))
  }

  const focusSibling = (sibling) => {
    setTimeout(() => sibling.focus(), 0)
  }
</script>

<div bind:this={inputContainer} class="mt-4 flex w-1/2 min-w-fit justify-between gap-3">
  {#each codeInputs as codeInput (codeInput)}
    <input
      required={required}
      type="text"
      maxlength="1"
      class="h-12 w-12 px-0 text-center text-base font-medium caret-transparent"
      on:paste|preventDefault={handlePaste}
      on:keydown={handleInput}
      oninput="this.value =this.value.replace(/[^0-9.]/g, '').replace(/(..*?)..*/g,'$1');"
      name={`code${codeInput}`}
    />
  {/each}
</div>
