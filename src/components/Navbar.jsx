import './Navbar.css'

export default function Navbar({ user, page, setPage, cartCount, onLogout }) {
  const isAdmin = user?.roles?.includes('ROLE_ADMIN')

  return (
    <nav className="nav">
      <div className="nav-logo" onClick={() => setPage('shop')}>
        Rouge<span className="nav-logo-dot">.</span>
      </div>

      <div className="nav-links">
        <button
          className={`nav-link ${page === 'shop' ? 'active' : ''}`}
          onClick={() => setPage('shop')}
        >
          Boutique
        </button>

        {user && (
          <button
            className={`nav-link ${page === 'orders' ? 'active' : ''}`}
            onClick={() => setPage('orders')}
          >
            Mes commandes
          </button>
        )}

        {isAdmin && (
          <button
            className={`nav-link ${page === 'admin' ? 'active' : ''}`}
            onClick={() => setPage('admin')}
          >
            Admin
          </button>
        )}

        {user ? (
          <button className="nav-link" onClick={onLogout}>
            Déconnexion
          </button>
        ) : (
          <button
            className={`nav-link ${page === 'auth' ? 'active' : ''}`}
            onClick={() => setPage('auth')}
          >
            Connexion
          </button>
        )}

        {user && (
          <button className="nav-link nav-cart-btn" onClick={() => setPage('cart')}>
            🛒 {cartCount > 0 && <span className="badge">{cartCount}</span>}
          </button>
        )}
      </div>
    </nav>
  )
}
