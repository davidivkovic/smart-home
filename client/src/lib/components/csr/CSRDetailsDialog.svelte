<script>
  export let csr;
  export let close
</script>

<div>{csr.commonName}</div>
<button on:click={close} class="primary">Close</button>