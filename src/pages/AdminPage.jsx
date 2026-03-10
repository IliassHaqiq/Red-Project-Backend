import { useState } from 'react'
import AdminProducts  from './admin/AdminProducts'
import AdminOrders    from './admin/AdminOrders'
import AdminCategories from './admin/AdminCategories'
import './AdminPage.css'

const NAV_ITEMS = [
  { key: 'products',   icon: '📦', label: 'Produits'    },
  { key: 'orders',     icon: '📋', label: 'Commandes'   },
  { key: 'categories', icon: '🏷️', label: 'Catégories'  },
]

export default function AdminPage({ token, addToast }) {
  const [section, setSection] = useState('products')

  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-title">Administration</div>
        {NAV_ITEMS.map(n => (
          <button
            key={n.key}
            className={`admin-nav-item ${section === n.key ? 'active' : ''}`}
            onClick={() => setSection(n.key)}
          >
            <span>{n.icon}</span> {n.label}
          </button>
        ))}
      </aside>

      <div className="admin-content fade-in" key={section}>
        {section === 'products'   && <AdminProducts   token={token} addToast={addToast} />}
        {section === 'orders'     && <AdminOrders     token={token} addToast={addToast} />}
        {section === 'categories' && <AdminCategories token={token} addToast={addToast} />}
      </div>
    </div>
  )
}
