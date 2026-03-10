import { useState } from 'react'
import Navbar            from './components/Navbar'
import Toast             from './components/Toast'
import { useToast }      from './hooks/useToast'
import AuthPage          from './pages/AuthPage'
import ShopPage          from './pages/ShopPage'
import ProductDetailPage from './pages/ProductDetailPage'
import CartPage          from './pages/CartPage'
import OrdersPage        from './pages/OrdersPage'
import AdminPage         from './pages/AdminPage'

export default function App() {
  const [page, setPage]               = useState('shop')
  const [user, setUser]               = useState(null)
  const [token, setToken]             = useState(null)
  const [cart, setCart]               = useState([])
  const [detailProduct, setDetailProduct] = useState(null)
  const { toasts, addToast }          = useToast()

  // ── Auth ───────────────────────────────────────────────────────────────────
  const handleLogin = (data) => {
    setToken(data.token)
    setUser(data)
    setPage('shop')
  }

  const handleLogout = () => {
    setToken(null)
    setUser(null)
    setCart([])
    setPage('shop')
    addToast('Déconnecté')
  }

  // ── Cart ───────────────────────────────────────────────────────────────────
  const addToCart = (product, qty) => {
    setCart(prev => {
      const existing = prev.find(i => i.id === product.id)
      if (existing) return prev.map(i => i.id === product.id ? { ...i, qty: i.qty + qty } : i)
      return [...prev, { ...product, qty }]
    })
  }

  const cartCount  = cart.reduce((sum, i) => sum + i.qty, 0)
  const isAdmin    = user?.roles?.includes('ROLE_ADMIN')

  // ── Router ─────────────────────────────────────────────────────────────────
  const renderPage = () => {
    switch (page) {
      case 'auth':
        return <AuthPage onLogin={handleLogin} addToast={addToast} />

      case 'shop':
        return (
          <ShopPage
            token={token}
            onAddToCart={addToCart}
            addToast={addToast}
            setPage={setPage}
            setDetailProduct={setDetailProduct}
          />
        )

      case 'detail':
        return (
          <ProductDetailPage
            product={detailProduct}
            token={token}
            onAddToCart={addToCart}
            setPage={setPage}
            addToast={addToast}
          />
        )

      case 'cart':
        return (
          <CartPage
            cart={cart}
            setCart={setCart}
            token={token}
            addToast={addToast}
            setPage={setPage}
          />
        )

      case 'orders':
        return token
          ? <OrdersPage token={token} />
          : <AuthPage onLogin={handleLogin} addToast={addToast} />

      case 'admin':
        return isAdmin
          ? <AdminPage token={token} addToast={addToast} />
          : <ShopPage token={token} onAddToCart={addToCart} addToast={addToast} setPage={setPage} setDetailProduct={setDetailProduct} />

      default:
        return null
    }
  }

  return (
    <>
      {page !== 'auth' && (
        <Navbar
          user={user}
          page={page}
          setPage={setPage}
          cartCount={cartCount}
          onLogout={handleLogout}
        />
      )}

      {renderPage()}

      <Toast toasts={toasts} />
    </>
  )
}
