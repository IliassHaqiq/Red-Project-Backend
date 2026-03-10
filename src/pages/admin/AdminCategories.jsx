import { useState, useEffect } from 'react'
import { getCategories, createCategory } from '../../api'

export default function AdminCategories({ token, addToast }) {
  const [categories, setCategories] = useState([])
  const [loading, setLoading]       = useState(true)
  const [name, setName]             = useState('')
  const [saving, setSaving]         = useState(false)
  const [error, setError]           = useState('')

  const load = () => {
    setLoading(true)
    getCategories().then(setCategories).finally(() => setLoading(false))
  }
  useEffect(load, [])

  const create = async () => {
    if (!name.trim()) return
    setSaving(true); setError('')
    try {
      await createCategory({ name }, token)
      addToast('Catégorie créée')
      setName('')
      load()
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <h2 className="section-title">Catégories</h2>
      <div className="divider" />

      <div className="card" style={{ padding: 28, maxWidth: 480, marginBottom: 32 }}>
        <div style={{ fontWeight: 500, marginBottom: 16 }}>Nouvelle catégorie</div>
        {error && <div className="alert alert-error">{error}</div>}
        <div style={{ display: 'flex', gap: 12 }}>
          <input
            className="input"
            placeholder="Nom de la catégorie"
            value={name}
            onChange={e => setName(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && create()}
          />
          <button className="btn btn-primary" onClick={create} disabled={saving}>
            {saving ? <span className="spinner" /> : 'Créer'}
          </button>
        </div>
      </div>

      {loading ? <div className="spinner" /> : (
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 10 }}>
          {categories.map(c => (
            <div key={c.id} className="card" style={{ padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 10 }}>
              <span style={{ fontSize: 14, fontWeight: 500 }}>{c.name}</span>
              <span style={{ color: 'var(--muted)', fontSize: 12 }}>#{c.id}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
