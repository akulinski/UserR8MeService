import React, {Component} from 'react';
import './App.css';
import {TableComponent} from "./components/TableComponent/table-component";
import axios from 'axios';
import TextField from "@material-ui/core/TextField";
import {IOptions as classes} from "glob";

class App extends Component {

  constructor(props) {
    super(props);

    localStorage.setItem('jwtToken', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MzM3NjQzMDk3MywiaWF0IjoxNTc2NDMwOTczfQ.WKReYYSW0LtGFpGZNTGTUT1NaTXZLkgO-2CWq7gv7E_JaQreM-WpNlIaeYlDh9qd2IeRuBtq4Qw-4MFGdRcEfA');

    axios.defaults.headers.common['Authorization'] =
      'Bearer ' + localStorage.getItem('jwtToken');

    this.state = {
      users: [],
      displayedUsers: []
    }
  }

  filter = e =>{
    const {users} = this.state;

    let displayed = users.filter(user=> user.username.toLocaleLowerCase() === e.target.value.toLowerCase());

    this.setState({displayedUsers: displayed});
  };

  componentDidMount() {
    axios.get(`http://localhost:8080/api/v1/user/all`)
      .then(res => {
        const users = res.data;
        this.setState({ users: users });
      })
  }

  render() {
    return (
      <div className="App">
        <form noValidate autoComplete="off">
          <TextField id="outlined" label="Search" variant="outlined" onChange={this.filter}/>
        </form>
        <TableComponent users={this.state.displayedUsers}/>
      </div>
    );
  }
}

export default App;
