import PropTypes from 'prop-types';
import '../styles/MappingsTable.css';

export default function MappingsTable({
  mappings,
  onRowClick,
  onFlagClick,
  loading,
}) {
  if (loading) {
    return <div className="table-loading">Loading...</div>;
  }

  if (mappings.length === 0) {
    return (
      <div className="table-empty">
        <p>No mappings found. Try adjusting your search or filters.</p>
      </div>
    );
  }

  return (
    <div className="mappings-table-wrapper">
      <table className="mappings-table">
        <thead>
          <tr>
            <th className="col-flag"></th>
            <th className="col-name">Mapping Name</th>
            <th className="col-source">Source System</th>
            <th className="col-target">Target System</th>
            <th className="col-type">Type</th>
            <th className="col-complexity">Complexity</th>
            <th className="col-status">Status</th>
            <th className="col-created">Created</th>
          </tr>
        </thead>
        <tbody>
          {mappings.map((mapping) => (
            <tr
              key={mapping.id}
              className="table-row"
              onClick={() => onRowClick(mapping)}
            >
              <td className="col-flag">
                <button
                  className={`flag-btn ${mapping.flagged ? 'flagged' : ''}`}
                  onClick={(e) => {
                    e.stopPropagation();
                    onFlagClick(mapping.id, mapping.flagged);
                  }}
                  title={mapping.flagged ? 'Unflag' : 'Flag for review'}
                >
                  ⚑
                </button>
              </td>
              <td className="col-name">
                <span className="mapping-name">{mapping.name}</span>
              </td>
              <td className="col-source">{mapping.sourceSystem}</td>
              <td className="col-target">{mapping.targetSystem}</td>
              <td className="col-type">
                <span className={`badge type-${mapping.transformationType}`}>
                  {mapping.transformationType || 'N/A'}
                </span>
              </td>
              <td className="col-complexity">
                <span className={`badge complexity-${mapping.complexity}`}>
                  {mapping.complexity || 'Medium'}
                </span>
              </td>
              <td className="col-status">
                <span className={`badge status-${mapping.status}`}>
                  {mapping.status || 'Active'}
                </span>
              </td>
              <td className="col-created">
                {mapping.createdAt
                  ? new Date(mapping.createdAt).toLocaleDateString()
                  : 'N/A'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

MappingsTable.propTypes = {
  mappings: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      name: PropTypes.string,
      sourceSystem: PropTypes.string,
      targetSystem: PropTypes.string,
      transformationType: PropTypes.string,
      complexity: PropTypes.string,
      status: PropTypes.string,
      flagged: PropTypes.bool,
      createdAt: PropTypes.string,
    })
  ),
  onRowClick: PropTypes.func.isRequired,
  onFlagClick: PropTypes.func.isRequired,
  loading: PropTypes.bool,
};

MappingsTable.defaultProps = {
  mappings: [],
  loading: false,
};
