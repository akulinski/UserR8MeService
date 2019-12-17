import React from 'react';
import "./table-component-styles.css"
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableContainer from "@material-ui/core/TableContainer";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";

export const TableComponent = props => {
  return (
    <TableContainer className="users-table">
      <Table aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell align="left">username</TableCell>
            <TableCell align="left">link</TableCell>
            <TableCell align="left">current rating</TableCell>
            <TableCell align="left">enabled</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {props.users.map(row => (
            <TableRow key={row.username}>
              <TableCell component="th" scope="row" align="left">
                {row.username}
              </TableCell>
              <TableCell align="left"><img src={row.link} alt=''/></TableCell>
              <TableCell align="left">{row.currentRating}</TableCell>
              <TableCell align="left">{row.enabled? "yes": "no"}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
