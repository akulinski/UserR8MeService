declare module namespace {

  export interface Rate {
    rate: number;
    sender?: any;
    question: string;
  }

  export interface RatesMap {
  }

  export interface Comment {
    comment: string;
    commenter?: any;
  }

  export interface Authority {
    authorityType: string;
    authority: string;
  }

  export interface UserDto {
    username: string;
    created?: any;
    link?: any;
    currentRating: number;
    enabled: boolean;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    credentialsNonExpired: boolean;
    new: boolean;
  }

}

