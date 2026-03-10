const BASE_URL = 'http://localhost:8080/api'

/**
 * Wrapper fetch générique
 * @param {string} path       - ex: "/products"
 * @param {object} options    - options fetch (method, body…)
 * @param {string|null} token - JWT token
 */
export async function request(path, options = {}, token = null) {
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  }

  const response = await fetch(`${BASE_URL}${path}`, { headers, ...options })

  if (!response.ok) {
    const err = await response.json().catch(() => ({ message: 'Erreur serveur' }))
    throw new Error(err.message || 'Erreur')
  }

  if (response.status === 204) return null
  return response.json()
}

// ── Auth ──────────────────────────────────────────────────────────────────────
export const login    = (body)        => request('/auth/login',    { method: 'POST', body: JSON.stringify(body) })
export const register = (body)        => request('/auth/register', { method: 'POST', body: JSON.stringify(body) })

// ── Products ──────────────────────────────────────────────────────────────────
export const getProducts  = ()           => request('/products')
export const getProduct   = (id)         => request(`/products/${id}`)
export const createProduct = (body, token) => request('/admin/products',     { method: 'POST',   body: JSON.stringify(body) }, token)
export const updateProduct = (id, body, token) => request(`/admin/products/${id}`, { method: 'PUT',    body: JSON.stringify(body) }, token)
export const deleteProduct = (id, token) => request(`/admin/products/${id}`, { method: 'DELETE' }, token)

// ── Categories ────────────────────────────────────────────────────────────────
export const getCategories    = ()           => request('/categories')
export const createCategory   = (body, token) => request('/admin/categories', { method: 'POST', body: JSON.stringify(body) }, token)

// ── Orders ────────────────────────────────────────────────────────────────────
export const createOrder      = (body, token) => request('/orders',       { method: 'POST',  body: JSON.stringify(body) }, token)
export const getMyOrders      = (token)        => request('/orders/me',   {}, token)
export const getAllOrders      = (token)        => request('/admin/orders', {}, token)
export const updateOrderStatus = (id, status, token) =>
  request(`/admin/orders/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }, token)
