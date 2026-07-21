import { createVNode, render } from 'vue'
import CustomErrorToast from '../components/CustomErrorToast.vue'

function showToast(message, duration = 5000, type = 'error') {
  const container = document.createElement('div')
  container.className = 'custom-error-toast-container'

  const vnode = createVNode(CustomErrorToast, {
    message,
    duration,
    type,
    onClose: () => {
      container.style.transition = 'opacity 0.3s ease-out, transform 0.3s ease-out'
      container.style.opacity = '0'
      container.style.transform = 'translateY(-100%)'

      setTimeout(() => {
        render(null, container)
        if (container.parentNode) {
          container.parentNode.removeChild(container)
        }
      }, 300)
    }
  })

  document.body.appendChild(container)
  render(vnode, container)
}

export function showError(message, duration = 5000) {
  showToast(message, duration, 'error')
}

export function showSuccess(message, duration = 5000) {
  showToast(message, duration, 'success')
}