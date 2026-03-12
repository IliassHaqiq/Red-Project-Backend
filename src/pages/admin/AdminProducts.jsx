import { useState, useEffect } from 'react'
import { getProducts, getCategories, createProduct, updateProduct, deleteProduct } from '../../api'

export default function AdminProducts({ token, addToast }) {
  const [products, setProducts]     = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading]       = useState(true)
  const [modal, setModal]           = useState(null)   // null | 'create' | product object
  const [form, setForm] = useState({
    name: '',
    description: '',
    price: '',
    stock: '',
    categoryId: '',
    images: [{ imageUrl: '', primaryImage: true, displayOrder: 1 }]
  })
  const [saving, setSaving]         = useState(false)
  const [error, setError]           = useState('')

  const load = () => {
    setLoading(true)
    Promise.all([getProducts(), getCategories()])
      .then(([p, c]) => { setProducts(p); setCategories(c) })
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const openCreate = () => {
    setForm({
      name: '',
      description: '',
      price: '',
      stock: '',
      categoryId: '',
      images: [{ imageUrl: '', primaryImage: true, displayOrder: 1 }]
    })
    setError('')
    setModal('create')
  }

  const openEdit = (p) => {
    setForm({
      name: p.name,
      description: p.description || '',
      price: p.price,
      stock: p.stock,
      categoryId: categories.find(c => c.name === p.category)?.id || '',
      images: p.images?.length
        ? p.images.map((img, index) => ({
            imageUrl: img.imageUrl,
            primaryImage: !!img.primaryImage,
            displayOrder: img.displayOrder ?? index + 1
          }))
        : [{ imageUrl: '', primaryImage: true, displayOrder: 1 }]
    })
    setError('')
    setModal(p)
  }

  const save = async () => {
    setSaving(true); setError('')
    try {
      const body = {
        name: form.name,
        description: form.description,
        price: parseFloat(form.price),
        stock: parseInt(form.stock),
        categoryId: parseInt(form.categoryId),
        images: form.images
          .filter(img => img.imageUrl.trim() !== '')
          .map((img, index) => ({
            imageUrl: img.imageUrl.trim(),
            primaryImage: img.primaryImage,
            displayOrder: index + 1
          }))
      }
      if (modal === 'create') {
        await createProduct(body, token)
        addToast('Produit créé')
      } else {
        await updateProduct(modal.id, body, token)
        addToast('Produit mis à jour')
      }
      setModal(null)
      load()
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  const del = async (id) => {
    if (!confirm('Supprimer ce produit ?')) return
    await deleteProduct(id, token)
    addToast('Produit supprimé')
    load()
  }

  const set = (key) => (e) => setForm(f => ({ ...f, [key]: e.target.value }))
  const setImageField = (index, key, value) => {
    setForm(f => ({
      ...f,
      images: f.images.map((img, i) =>
        i === index ? { ...img, [key]: value } : img
      )
    }))
  }
  
  const addImageField = () => {
    setForm(f => ({
      ...f,
      images: [
        ...f.images,
        {
          imageUrl: '',
          primaryImage: false,
          displayOrder: f.images.length + 1
        }
      ]
    }))
  }
  
  const removeImageField = (index) => {
    setForm(f => {
      const updated = f.images.filter((_, i) => i !== index)
        .map((img, i) => ({
          ...img,
          displayOrder: i + 1
        }))
  
      if (updated.length > 0 && !updated.some(img => img.primaryImage)) {
        updated[0].primaryImage = true
      }
  
      return {
        ...f,
        images: updated.length
          ? updated
          : [{ imageUrl: '', primaryImage: true, displayOrder: 1 }]
      }
    })
  }
  
  const setPrimaryImage = (index) => {
    setForm(f => ({
      ...f,
      images: f.images.map((img, i) => ({
        ...img,
        primaryImage: i === index
      }))
    }))
  }

  return (
    <div>
      <div className="section-header">
        <div>
          <h2 className="section-title">Produits</h2>
          <div className="divider" />
        </div>
        <button className="btn btn-primary" onClick={openCreate}>+ Nouveau produit</button>
      </div>

      {loading ? <div className="spinner" /> : (
        <div className="card">
          <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Image</th>
              <th>Nom</th>
              <th>Catégorie</th>
              <th>Prix</th>
              <th>Stock</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map(p => {
              const primaryImage =
                p.images?.find(img => img.primaryImage)?.imageUrl ||
                p.images?.[0]?.imageUrl ||
                ''

              return (
                <tr key={p.id}>
                  <td>#{p.id}</td>
                  <td>
                    {primaryImage ? (
                      <img
                        src={primaryImage}
                        alt={p.name}
                        className="admin-product-thumb"
                      />
                    ) : (
                      <div className="admin-product-thumb admin-product-thumb-empty">
                        —
                      </div>
                    )}
                  </td>
                  <td>{p.name}</td>
                  <td>{p.category}</td>
                  <td>{Number(p.price).toFixed(2)} €</td>
                  <td>{p.stock}</td>
                  <td>
                    <button onClick={() => openEdit(p)}>Modifier</button>
                    <button onClick={() => del(p.id)}>Supprimer</button>
                  </td>
                </tr>
              )
            })}
          </tbody>
          </table>
        </div>
      )}

      {/* Modal */}
      {modal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModal(null)}>
          <div className="modal">
            <h3 style={{ fontFamily: "'Cormorant Garamond', serif", fontSize: 24, marginBottom: 24 }}>
              {modal === 'create' ? 'Nouveau produit' : `Modifier : ${modal.name}`}
            </h3>
            {error && <div className="alert alert-error">{error}</div>}

            <div className="field">
              <label className="label">Nom</label>
              <input className="input" value={form.name} onChange={set('name')} />
            </div>
            <div className="field">
              <label className="label">Description</label>
              <textarea className="input" rows={3} value={form.description} onChange={set('description')} />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
              <div className="field">
                <label className="label">Prix (€)</label>
                <input className="input" type="number" step="0.01" value={form.price} onChange={set('price')} />
              </div>
              <div className="field">
                <label className="label">Stock</label>
                <input className="input" type="number" value={form.stock} onChange={set('stock')} />
              </div>
            </div>
            <div className="field">
              <label className="label">Catégorie</label>
              <select className="input" value={form.categoryId} onChange={set('categoryId')}>
                <option value="">Sélectionner...</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
            <div className="field">
              <label className="label">Images du produit</label>

              <div className="admin-images-list">
                {form.images.map((img, index) => (
                  <div key={index} className="admin-image-row">
                    <input
                      className="input"
                      type="text"
                      placeholder="https://..."
                      value={img.imageUrl}
                      onChange={(e) => setImageField(index, 'imageUrl', e.target.value)}
                    />

                    <label className="admin-image-primary">
                      <input
                        type="radio"
                        name="primaryImage"
                        checked={img.primaryImage}
                        onChange={() => setPrimaryImage(index)}
                      />
                      Principale
                    </label>

                    <button
                      type="button"
                      className="btn btn-ghost"
                      onClick={() => removeImageField(index)}
                    >
                      Supprimer
                    </button>

                    {img.imageUrl && (
                      <img
                        src={img.imageUrl}
                        alt={`Prévisualisation ${index + 1}`}
                        className="admin-image-preview"
                      />
                    )}
                  </div>
                ))}
              </div>

              <button type="button" className="btn btn-secondary" onClick={addImageField}>
                + Ajouter une image
              </button>
            </div>
            <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end', marginTop: 8 }}>
              <button className="btn btn-ghost" onClick={() => setModal(null)}>Annuler</button>
              <button className="btn btn-primary" onClick={save} disabled={saving}>
                {saving ? <span className="spinner" /> : 'Enregistrer'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
