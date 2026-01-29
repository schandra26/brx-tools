import PropTypes from 'prop-types';
import '../styles/FilterPanel.css';

export default function FilterPanel({ filters, setFilters, mappings }) {
  const getUniqueValues = (key) => {
    return [...new Set(mappings.map((m) => m[key]).filter(Boolean))].sort();
  };

  const transformationTypes = getUniqueValues('transformationType');
  const complexityLevels = getUniqueValues('complexity');
  const statuses = getUniqueValues('status');
  const sourceSystems = getUniqueValues('sourceSystem');

  const handleFilterChange = (filterName, value, isChecked) => {
    setFilters((prev) => ({
      ...prev,
      [filterName]: isChecked
        ? [...prev[filterName], value]
        : prev[filterName].filter((v) => v !== value),
    }));
  };

  const handleClearFilters = () => {
    setFilters({
      transformationType: [],
      complexity: [],
      status: [],
      sourceSystem: [],
    });
  };

  const activeFiltersCount =
    Object.values(filters).reduce((acc, arr) => acc + arr.length, 0);

  return (
    <div className="filter-panel">
      <div className="filter-header">
        <h3>Filters</h3>
        {activeFiltersCount > 0 && (
          <button className="clear-filters-btn" onClick={handleClearFilters}>
            Clear ({activeFiltersCount})
          </button>
        )}
      </div>

      <div className="filter-group">
        <h4>Transformation Type</h4>
        {transformationTypes.length > 0 ? (
          <div className="filter-options">
            {transformationTypes.map((type) => (
              <label key={type} className="filter-option">
                <input
                  type="checkbox"
                  checked={filters.transformationType.includes(type)}
                  onChange={(e) =>
                    handleFilterChange(
                      'transformationType',
                      type,
                      e.target.checked
                    )
                  }
                />
                <span>{type}</span>
              </label>
            ))}
          </div>
        ) : (
          <p className="no-options">No data available</p>
        )}
      </div>

      <div className="filter-group">
        <h4>Complexity</h4>
        {complexityLevels.length > 0 ? (
          <div className="filter-options">
            {complexityLevels.map((level) => (
              <label key={level} className="filter-option">
                <input
                  type="checkbox"
                  checked={filters.complexity.includes(level)}
                  onChange={(e) =>
                    handleFilterChange('complexity', level, e.target.checked)
                  }
                />
                <span>{level}</span>
              </label>
            ))}
          </div>
        ) : (
          <p className="no-options">No data available</p>
        )}
      </div>

      <div className="filter-group">
        <h4>Status</h4>
        {statuses.length > 0 ? (
          <div className="filter-options">
            {statuses.map((status) => (
              <label key={status} className="filter-option">
                <input
                  type="checkbox"
                  checked={filters.status.includes(status)}
                  onChange={(e) =>
                    handleFilterChange('status', status, e.target.checked)
                  }
                />
                <span>{status}</span>
              </label>
            ))}
          </div>
        ) : (
          <p className="no-options">No data available</p>
        )}
      </div>

      <div className="filter-group">
        <h4>Source System</h4>
        {sourceSystems.length > 0 ? (
          <div className="filter-options">
            {sourceSystems.slice(0, 10).map((system) => (
              <label key={system} className="filter-option">
                <input
                  type="checkbox"
                  checked={filters.sourceSystem.includes(system)}
                  onChange={(e) =>
                    handleFilterChange('sourceSystem', system, e.target.checked)
                  }
                />
                <span>{system}</span>
              </label>
            ))}
            {sourceSystems.length > 10 && (
              <p className="more-options">
                +{sourceSystems.length - 10} more
              </p>
            )}
          </div>
        ) : (
          <p className="no-options">No data available</p>
        )}
      </div>
    </div>
  );
}

FilterPanel.propTypes = {
  filters: PropTypes.shape({
    transformationType: PropTypes.array,
    complexity: PropTypes.array,
    status: PropTypes.array,
    sourceSystem: PropTypes.array,
  }).isRequired,
  setFilters: PropTypes.func.isRequired,
  mappings: PropTypes.array.isRequired,
};
