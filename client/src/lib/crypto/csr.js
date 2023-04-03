import { Pkcs10CertificateRequestGenerator, Pkcs10CertificateRequest } from '@peculiar/x509'

const signingAlgorithm = 'RSASSA-PKCS1-v1_5'
const hashAlgorithm = 'SHA-256'

export const RDN = {
  commonName: 'CN',
  country: 'C',
  state: 'ST',
  locality: 'L',
  organization: 'O',
  organizationalUnit: 'OU',
  givenName: 'G',
  surname: 'SN',
  email: 'E'
}

/** @param {RDN} RDNs */
export const createCSR = async RDNs => {

  const keys = await crypto.subtle.generateKey(
    {
      name: signingAlgorithm,
      modulusLength: 1024,
      publicExponent: new Uint8Array([1, 0, 1]),
      hash: hashAlgorithm
    },
    true,
    ['sign', 'verify']
  )

  const csr = await Pkcs10CertificateRequestGenerator.create({
    name: Object.entries(RDNs).map(([rdn, value]) => ({ [rdn]: [{ utf8String: value }] })),
    keys,
    signingAlgorithm
  })

  return {
    publicKey: keyToPEM(await crypto.subtle.exportKey('spki', keys.publicKey), 'PUBLIC KEY'),
    privateKey: keyToPEM(await crypto.subtle.exportKey('pkcs8', keys.privateKey), 'PRIVATE KEY'),
    PEM: toPEM(csr, 'CERTIFICATE REQUEST')
  }
}

function keyToPEM(buffer, type) {
  const base64 = window.btoa(String.fromCharCode.apply(null, new Uint8Array(buffer))).replace(/(.{64})/g, '$1\n')
  return `-----BEGIN ${type}-----\n${base64}\n-----END ${type}-----`
}

const toPEM = (data, type) => {
  const base64 = data.toString("base64").replace(/(.{64})/g, '$1\n')
  return `-----BEGIN ${type}-----\n${base64}\n-----END ${type}-----`
}

/**
 * 
 * @param {string} pem 
 * @returns {RDN}
 */
export const CSRFromPEM = pem => {
  const csr = new Pkcs10CertificateRequest(pem)
  return Object.entries(RDN).reduce((acc, [rdn, value]) => ({ [rdn]: csr.subjectName.getField(value)[0], ...acc }), {})
}