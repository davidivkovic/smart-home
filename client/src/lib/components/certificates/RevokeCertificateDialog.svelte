<script>
  import { revoke } from '$lib/api/certificates'
  export let certificate
  export let close
  const crlReasons = [
    {
      name: 'Unspecified',
      value: 0
    },
    {
      name: 'Key Compromise',
      value: 1
    },
    {
      name: 'CA Compromise',
      value: 2
    },
    {
      name: 'Affiliation Changed',
      value: 3
    },
    {
      name: 'Superseded',
      value: 4
    },
    {
      name: 'Cessation of Operation',
      value: 5
    },
    {
      name: 'Certificate Hold',
      value: 6
    },
    {
      name: 'Remove from CRL',
      value: 8
    },
    {
      name: 'Privilege Withdrawn',
      value: 9
    },
    {
      name: 'AA Compromise',
      value: 10
    }
  ]
  let crlReason = -1
  const confirm = async () => {
    try {
      await revoke(certificate.serialNumber, crlReason)
      close()
    } catch (error) {
      console.error(error)
    }
  }
</script>

<div class="px-1 py-1">
  <h3>Revoke Certificate</h3>
  <p class="text-sm -mt-1">Are you sure you want to revoke this certificate?</p>

  <div class="mt-4">
    <h4 class="text-[15px] font-medium mb-2">Please Specify a CRL reason</h4>
    {#each crlReasons as reason}
      <div class="w-full flex items-center">
        <label for="crl-reason-{reason.value}" class="block w-full py-1 cursor-pointer text-[13px] font-medium">{reason.name}</label>
        <input 
          name="crl-reason" 
          bind:group={crlReason} 
          type="radio" 
          id="crl-reason-{reason.value}" 
          value={reason.value} 
          class="cursor-pointer"
        />
      </div>
    {/each}
  </div>

  <div class="flex mt-6">
    <button 
      class="primary mr-1.5 !text-sm flex-1" 
      on:click={confirm}
      disabled={crlReason === -1}
    >
      Revoke Certificate
    </button>
    <button class="secondary !text-sm px-10" on:click={close}>Cancel</button>
  </div>
</div>