import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import GoogleSignInPlugin from 'vue3-google-signin'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(GoogleSignInPlugin, {
    clientId: '239106531712-f1if0c9rnbcnimm30vbumnj7cr6abk0b.apps.googleusercontent.com',
})

app.mount('#app')
