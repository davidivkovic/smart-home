<h1><img src="https://github.com/davidivkovic/smart-home/assets/16724395/914ba50e-a64d-4338-894a-437c842d3b6d" align="center" width="60" height="54"/> Smart Home </h1> 

A single place for all the smart and connected devices in your home. Serves as a Public Key Infrastructure and HomeKit clone.

## Built using 🔧

- [Quarkus + Panache](https://quarkus.io/)
- [MongoDB](https://mongodb.com/)
- [SvelteKit](https://kit.svelte.dev) 
- [TailwindCSS](https://tailwindcss.com/)
- [Docker](https://www.docker.com/)

## Showcase 📷
Please make sure to turn on the sound 🔊

https://github.com/davidivkovic/smart-home/assets/16724395/82661b29-679b-43ed-a60e-903f6a1508b7

## Legal 💼
I am not affiliated, associated, authorized, endorsed by, or in any way officially connected to smarthome.com, apple.com or HomeKit.

All product and company names are trademarks™ or registered® trademarks of their respective holders. Use of them does not imply any affiliation with or endorsement by them.

## How to run⚡

Position terminal at root
1. run MongoDB
```
docker compose up -d
```
2. run the public key infrastructure
```
./smart-home-pki/gradlew quarkusDev
```
3. Run the devices service
```
./smart-home-devices/gradlew quarkusDev
```
4. Install node.js dependencies and run the SvelteKit client @ localhost:5173
```
cd ./client
npm i
npm run dev
```
