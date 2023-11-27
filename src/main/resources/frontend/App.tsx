/**
 * npx create-react-app my-app
 * npm i axios
 * replace content of App.tsx with the following code for a quick test


import React, {useEffect, useState} from 'react';
import axios from "axios";
import './App.css';

export const App = () => {
    const [isGhLoggedIn, setIsGhLoggedIn] = useState<boolean>(false);
    const [isMsLoggedIn, setIsMsLoggedIn] = useState<boolean>(false);
    const [ghMessage, setGhMessage] = useState<string>();
    const [msMessage, setMsMessage] = useState<string>();

    const openGithubLogin = async () => {
        window.location.assign("http://localhost:8080/planner/oauth2/authorization/github")
    }
    const openMsLogin = async () => {
        window.location.assign("http://localhost:8080/planner/oauth2/authorization/aad")
    }

    const loadGithubFoo = async () => {
        await loadSomething("http://localhost:8080/planner/github/foo", "gh")
    }

    const loadMsFoo = async () => {
        await loadSomething("http://localhost:8080/planner/aad/foo", "ms")
    }

    const loadSomething = async (url: string, target: string) => {
        axios.get(url, {withCredentials: true})
            .then((res) => {
                if (res && res.data) {
                    if (target === "gh") {
                        setGhMessage(res.data.foo)
                    } else if (target === "ms") {
                        setMsMessage(res.data.foo)
                    }
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }

    const checkLogin = async (url: string) => {
        return axios.get(url, {withCredentials: true})
            .then((res) => {
                if (res && res.data) {
                    return res.data.loginActive
                } else {
                    return false
                }
            })
            .catch((error) => {
                console.log(error);
                return false
            });
    }

    useEffect(() => {
        checkLogin("http://localhost:8080/planner/auth/github/check")
            .then((res) => {
                setIsGhLoggedIn(res)
            })


        checkLogin("http://localhost:8080/planner/auth/aad/check")
            .then((res) => {
                setIsMsLoggedIn(res)
            })
    }, [isMsLoggedIn, isGhLoggedIn]);

    return (
        <div className="App">
            <div style={{padding: "2rem"}}>
                {!isGhLoggedIn && <button onClick={openGithubLogin}>Login to GitHub</button>}
                {isGhLoggedIn && <button onClick={loadGithubFoo}>Load something from GitHub</button>}
                {ghMessage && <div>{ghMessage}</div>}
            </div>
            <div style={{padding: "2rem"}}>
                {!isMsLoggedIn && <button onClick={openMsLogin}>Login to Microsoft</button>}
                {isMsLoggedIn && <button onClick={loadMsFoo}>Load something from Microsoft</button>}
                {msMessage && <div>{msMessage}</div>}
            </div>
        </div>
    );
}
 * */