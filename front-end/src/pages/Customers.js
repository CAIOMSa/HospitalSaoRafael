import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { customerApi } from '../services/api';
import './Customers.css';

function Customers() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    document: '',
    status: 'ACTIVE',
    address: {
      street: '',
      number: '',
      city: '',
      state: '',
      zipCode: ''
    }
  });

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    try {
      const response = await customerApi.list();
      setCustomers(response.data);
    } catch (error) {
      console.error('Error loading customers:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCustomer) {
        await customerApi.update(editingCustomer.id, formData);
      } else {
        await customerApi.create(formData);
      }
      loadCustomers();
      handleCloseModal();
    } catch (error) {
      console.error('Error saving customer:', error);
      alert('Erro ao salvar cliente');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await customerApi.delete(id);
        loadCustomers();
      } catch (error) {
        console.error('Error deleting customer:', error);
        alert('Erro ao excluir cliente');
      }
    }
  };

  const handleEdit = (customer) => {
    setEditingCustomer(customer);
    setFormData(customer);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingCustomer(null);
    setFormData({
      name: '',
      email: '',
      phone: '',
      document: '',
      status: 'ACTIVE',
      address: {
        street: '',
        number: '',
        city: '',
        state: '',
        zipCode: ''
      }
    });
  };

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="loading">
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Navbar />
      <div className="container">
        <div className="customers-header">
          <div>
            <h1>Clientes</h1>
            <p>Gerencie todos os clientes do sistema</p>
          </div>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            ➕ Novo Cliente
          </button>
        </div>

        <div className="card">
          <table className="customers-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Email</th>
                <th>Telefone</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((customer) => (
                <tr key={customer.id}>
                  <td>{customer.name}</td>
                  <td>{customer.email}</td>
                  <td>{customer.phone}</td>
                  <td>
                    <span className={`status-badge ${customer.status === 'ACTIVE' ? 'status-online' : 'status-offline'}`}>
                      {customer.status}
                    </span>
                  </td>
                  <td>
                    <div className="table-actions">
                      <button className="btn-icon" onClick={() => handleEdit(customer)}>
                        ✏️
                      </button>
                      <button className="btn-icon danger" onClick={() => handleDelete(customer.id)}>
                        🗑️
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {customers.length === 0 && (
            <div className="empty-state">
              <p>Nenhum cliente cadastrado</p>
            </div>
          )}
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingCustomer ? 'Editar Cliente' : 'Novo Cliente'}</h2>
              <button className="btn-close" onClick={handleCloseModal}>✕</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="form-group">
                  <label>Nome *</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Email *</label>
                  <input
                    type="email"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Telefone</label>
                  <input
                    type="text"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Documento</label>
                  <input
                    type="text"
                    value={formData.document}
                    onChange={(e) => setFormData({ ...formData, document: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Status</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  >
                    <option value="ACTIVE">Ativo</option>
                    <option value="INACTIVE">Inativo</option>
                  </select>
                </div>
              </div>
              
              <h3>Endereço</h3>
              <div className="form-grid">
                <div className="form-group">
                  <label>Rua</label>
                  <input
                    type="text"
                    value={formData.address.street}
                    onChange={(e) => setFormData({ 
                      ...formData, 
                      address: { ...formData.address, street: e.target.value }
                    })}
                  />
                </div>
                <div className="form-group">
                  <label>Número</label>
                  <input
                    type="text"
                    value={formData.address.number}
                    onChange={(e) => setFormData({ 
                      ...formData, 
                      address: { ...formData.address, number: e.target.value }
                    })}
                  />
                </div>
                <div className="form-group">
                  <label>Cidade</label>
                  <input
                    type="text"
                    value={formData.address.city}
                    onChange={(e) => setFormData({ 
                      ...formData, 
                      address: { ...formData.address, city: e.target.value }
                    })}
                  />
                </div>
                <div className="form-group">
                  <label>Estado</label>
                  <input
                    type="text"
                    value={formData.address.state}
                    onChange={(e) => setFormData({ 
                      ...formData, 
                      address: { ...formData.address, state: e.target.value }
                    })}
                  />
                </div>
                <div className="form-group">
                  <label>CEP</label>
                  <input
                    type="text"
                    value={formData.address.zipCode}
                    onChange={(e) => setFormData({ 
                      ...formData, 
                      address: { ...formData.address, zipCode: e.target.value }
                    })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={handleCloseModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingCustomer ? 'Atualizar' : 'Criar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Customers;
