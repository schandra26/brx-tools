import { useState, useEffect } from 'react';
import axios from 'axios';
import MappingsTable from '../components/MappingsTable';
import MappingDrawer from '../components/MappingDrawer';
import SearchBar from '../components/SearchBar';
import FilterPanel from '../components/FilterPanel';
import '../styles/QueryMappings.css';

export default function QueryMappings() {
  const [mappings, setMappings] = useState([]);
  const [filteredMappings, setFilteredMappings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    transformationType: [],
    complexity: [],
    status: [],
    sourceSystem: [],
  });
  const [selectedMapping, setSelectedMapping] = useState(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  useEffect(() => {
    fetchMappings();
  }, []);

  const fetchMappings = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8080/api/mappings');
      setMappings(response.data || []);
    } catch (err) {
      setError('Failed to load mappings: ' + err.message);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let filtered = mappings;

    // Apply search filter
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(
        (m) =>
          m.name?.toLowerCase().includes(term) ||
          m.sourceSystem?.toLowerCase().includes(term) ||
          m.targetSystem?.toLowerCase().includes(term) ||
          m.description?.toLowerCase().includes(term)
      );
    }

    // Apply category filters
    if (filters.transformationType.length > 0) {
      filtered = filtered.filter((m) =>
        filters.transformationType.includes(m.transformationType)
      );
    }
    if (filters.complexity.length > 0) {
      filtered = filtered.filter((m) =>
        filters.complexity.includes(m.complexity)
      );
    }
    if (filters.status.length > 0) {
      filtered = filtered.filter((m) =>
        filters.status.includes(m.status)
      );
    }
    if (filters.sourceSystem.length > 0) {
      filtered = filtered.filter((m) =>
        filters.sourceSystem.includes(m.sourceSystem)
      );
    }

    setFilteredMappings(filtered);
  }, [mappings, searchTerm, filters]);

  const handleRowClick = (mapping) => {
    setSelectedMapping(mapping);
    setDrawerOpen(true);
  };

  const handleCloseDrawer = () => {
    setDrawerOpen(false);
    setTimeout(() => setSelectedMapping(null), 300);
  };

  const handleFlagMapping = async (mappingId, isFlagged) => {
    try {
      await axios.patch(`http://localhost:8080/api/mappings/${mappingId}/flag`, {
        flagged: !isFlagged,
      });
      setMappings((prev) =>
        prev.map((m) =>
          m.id === mappingId ? { ...m, flagged: !isFlagged } : m
        )
      );
    } catch (err) {
      setError('Failed to update flag: ' + err.message);
    }
  };

  return (
    <div className="query-mappings">
      <div className="query-header">
        <h1>Mappings Query & Governance</h1>
        <p>Catalog and govern BRx Logical Model mappings</p>
      </div>

      <div className="query-container">
        <aside className="filter-sidebar">
          <FilterPanel
            filters={filters}
            setFilters={setFilters}
            mappings={mappings}
          />
        </aside>

        <main className="query-content">
          <SearchBar searchTerm={searchTerm} setSearchTerm={setSearchTerm} />

          {error && <div className="error-message">{error}</div>}

          {loading ? (
            <div className="loading">Loading mappings...</div>
          ) : (
            <div className="results-info">
              Showing {filteredMappings.length} of {mappings.length} mappings
            </div>
          )}

          <MappingsTable
            mappings={filteredMappings}
            onRowClick={handleRowClick}
            onFlagClick={handleFlagMapping}
            loading={loading}
          />
        </main>
      </div>

      {drawerOpen && selectedMapping && (
        <MappingDrawer
          mapping={selectedMapping}
          onClose={handleCloseDrawer}
          onFlag={(isFlagged) =>
            handleFlagMapping(selectedMapping.id, isFlagged)
          }
        />
      )}
    </div>
  );
}
